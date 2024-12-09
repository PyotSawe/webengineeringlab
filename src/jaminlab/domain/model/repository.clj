(ns jaminlab.domain.model.repository
  (:require [hugsql.core :as hugsql]
            [cheshire.core :as json]
            [taoensso.carmine :as car]
            [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]))

;; Redis connection setup
(def redis-conn {:pool {} :spec {:uri "redis://localhost:6379"}})

(defmacro wcar* [& body]
  `(car/wcar redis-conn ~@body))

;; HugSQL queries
(def user-queries
  ";; Insert a new user into the users table
   -- :user-id, :username, :email, :password, :roles
   INSERT INTO users (user_id, username, email, password, roles)
   VALUES (:user-id, :username, :email, :password, :roles);

   ;; Get a user by username
   -- :username is a required parameter
   SELECT * FROM users WHERE username = :username;

   ;; Create a session for a user
   -- :user-id and :session-token will be passed
   INSERT INTO sessions (user_id, session_token)
   VALUES (:user-id, :session-token);")

(hugsql/def-db-fns user-queries)

;; Redis Cache Utilities

(defn cache-get [key]
  (try
    (when-let [json-value (wcar* (car/get key))]
      (json/parse-string json-value true))
    (catch Exception e
      (log/error e "Failed to get key from Redis cache")
      nil)))

(defn cache-set [key value ttl]
  (try
    (wcar* (car/set key (json/generate-string value)))
    (wcar* (car/expire key ttl))
    (log/info "Cached" key "with TTL" ttl)
    true
    (catch Exception e
      (log/error e "Failed to set key in Redis cache")
      false)))

(defn cache-del [key]
  (try
    (wcar* (car/del key))
    (log/info "Deleted cache for" key)
    true
    (catch Exception e
      (log/error e "Failed to delete key from Redis cache")
      false)))

;; Advanced Caching Patterns

(defn lazy-cache-invalidate [key db-fn db-args]
  "Invalidate cache only when the associated database operation succeeds."
  (try
    (apply db-fn db-args)
    (cache-del key)
    true
    (catch Exception e
      (log/error e "Failed to invalidate cache lazily" {:key key})
      false)))

(defn write-through-cache [key value db-fn db-args ttl]
  "Write data to both the cache and database. Only cache if the DB operation succeeds."
  (try
    (apply db-fn db-args)
    (cache-set key value ttl)
    true
    (catch Exception e
      (log/error e "Write-through caching failed" {:key key :value value})
      false)))

;; Repository Functions

(defn insert-user [db user]
  (if (s/valid? ::schemas/user-db user)
    (let [cache-key (str "user:username:" (:username user))]
      (write-through-cache
       cache-key
       user
       hugsql/insert-user!
       [db user]
       3600))
    (throw (ex-info "Invalid user data" {:user user}))))

(defn get-user-by-username [db username]
  (let [cache-key (str "user:username:" username)
        cached-user (cache-get cache-key)]
    (if cached-user
      cached-user
      (let [user (hugsql/get-user-by-username db {:username username})]
        (if (s/valid? ::schemas/user-db user)
          (do
            (cache-set cache-key user 3600)
            user)
          (throw (ex-info "Invalid user data from DB" {:user user})))))))

(defn create-session [db session]
  (if (s/valid? ::schemas/session-db session)
    (let [cache-key (str "session:user:" (:user-id session))]
      (write-through-cache
       cache-key
       session
       hugsql/create-session!
       [db session]
       3600))
    (throw (ex-info "Invalid session data" {:session session}))))

;; Orders Functions

(defn save-order [order]
  (if-let [order-id (:id order)]
    (let [cache-key (str "order:id:" order-id)]
      (write-through-cache
       cache-key
       order
       (fn [order] (swap! orders assoc order-id order))
       [order]
       1800))
    (throw (ex-info "Invalid order data" {:order order}))))

(defn get-order [id]
  (let [cache-key (str "order:id:" id)
        cached-order (cache-get cache-key)]
    (if cached-order
      cached-order
      (if-let [order (@orders id)]
        (do
          (cache-set cache-key order 1800)
          order)
        nil))))

(defn all-orders []
  (let [order-keys (map #(str "order:id:" %) (keys @orders))
        cached-orders (->> (wcar* (apply car/mget order-keys))
                           (map #(when % (json/parse-string % true))))]
    (if (every? some? cached-orders)
      cached-orders
      (do
        (log/info "Falling back to in-memory orders")
        (vals @orders)))))

;; Lazy Cache Invalidation Example
(defn update-user-email [db user-id new-email]
  (let [cache-key (str "user:id:" user-id)]
    (lazy-cache-invalidate
     cache-key
     hugsql/update-user-email!
     [db {:user-id user-id :new-email new-email}])))

