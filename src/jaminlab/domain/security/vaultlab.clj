(ns jaminlab.domain.security.vaults
  (:require
   [buddy.hashers :as hashers]
   [buddy.core.crypto :as crypto]
   [clojure.java.jdbc :as jdbc]
   [clojure.tools.logging :as log]
   [clojure.string :as str]))

;; --- Database Configuration ---
(def db-spec
  {:dbtype "postgresql"
   :host "localhost"
   :port 5432
   :dbname "passwords_db"
   :user "dbuser"
   :password (System/getenv "DB_PASSWORD")
   :ssl true})

;; --- Secure Password Handling ---
(def post-quantum-hash-function :sha3-512) ;; Placeholder for post-quantum hash function

(defn hash-password [plain-password]
  (let [salt (crypto/rand-bytes 16)] ;; Generate a unique salt
    (hashers/derive plain-password {:salt salt :iterations 1000000 :algorithm :argon2i})))

(defn verify-password [plain-password hashed-password]
  (hashers/check plain-password hashed-password))

;; --- Store Password in DB ---
(defn store-password-in-db [user-id plain-password]
  (let [hashed-password (hash-password plain-password)
        salt (get hashed-password :salt)
        created-at (java.time.Instant/now)]
    (jdbc/insert! db-spec :user_passwords
                  {:user_id user-id
                   :hashed_password (:password hashed-password)
                   :salt salt
                   :created_at created-at})))

;; --- Rate Limiting Middleware ---
(def rate-limiter (atom {}))

(defn rate-limit [handler]
  (fn [request]
    (let [ip (get-in request [:headers "X-Forwarded-For"])]
      (if (rate-exceeded? ip)
        {:status 429 :body "Too many requests"}
        (do
          (reset-rate-limit ip)
          (handler request)))))

(defn rate-exceeded? [ip]
  (let [last-request-time (get @rate-limiter ip)
        now (System/currentTimeMillis)]
    (and last-request-time
         (< (- now last-request-time) 60000)
         (> (get @rate-limiter ip :count) 5))))

(defn reset-rate-limit [ip]
  (swap! rate-limiter assoc ip {:time (System/currentTimeMillis) :count (inc (get-in @rate-limiter [ip :count] 0))}))

;; --- Logging failed login attempts ---
(defn log-failed-login [user-id]
  (log/error (str "Failed login attempt for user: " user-id)))

(defn log-successful-login [user-id]
  (log/info (str "Successful login for user: " user-id)))

(defn check-password [user-id plain-password]
  (let [user (retrieve-password-from-db user-id)]
    (if user
      (let [hashed-password (:hashed_password user)]
        (if (verify-password plain-password hashed-password)
          (do
            (log-successful-login user-id)
            true)
          (do
            (log-failed-login user-id)
            false)))
      (do
        (log-failed-login user-id)
        false))))

;; --- Example Usage ---
(store-password-in-db 1 "securepassword123")
