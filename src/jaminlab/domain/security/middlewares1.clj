(ns jaminlab.domain.security.middlewares
  (:require
   [buddy.auth :as auth]
   [buddy.auth.middleware :as auth-middleware]
   [clojure.set :as set]
   [clojure.string :as s]
   [jaminlab.domain.security.jwt :as jwt]
   [jaminlab.domain.policies.abac :as abac]
   [clojure.java.io :as io]
   [clojure.tools.logging :as log]
   [clj-time.core :as t]
   [clj-time.format :as f]
   [clojure.tools.logging :as log]))

;; --- Helper Functions ---
(defn get-user-roles [request]
  (when-let [user (auth/current-authentication)]
    (:roles user)))

;; Rate limiting configuration
(def rate-limit-config
  {:limit 5                 ;; Maximum number of requests
   :window (t/minutes 1)    ;; Within a 1-minute window
   :cache (atom {})})       ;; In-memory cache for tracking requests

(defn current-time []
  (t/now))

(defn check-rate-limit [user-id]
  (let [{:keys [limit window cache]} rate-limit-config
        now (current-time)
        user-requests (get @cache user-id (sorted-map))]

    ;; Filter out old requests that are outside the time window
    (let [recent-requests (filter #(t/after? now (t/minus now window)) (vals user-requests))]
      (if (>= (count recent-requests) limit)
        {:status 429
         :body {:error "Rate limit exceeded. Try again later."}}
        (do
          ;; Add the current request timestamp
          (swap! cache assoc user-id (conj recent-requests now))
          nil)))) ;; Return nil if rate limit is not exceeded

;; Rate limit middleware
(defn rate-limit [handler]
  (fn [request]
    (let [user-id (get-in request [:body :user-id])]
      (if user-id
        (let [rate-limit-result (check-rate-limit user-id)]
          (if rate-limit-result
            rate-limit-result
            (handler request))) ;; Proceed with the handler if rate limit is not exceeded
        (handler request)))))

;; Authenticate requests using JWT from Authorization header
(defn wrap-jwt-auth [handler]
  (auth-middleware/wrap-authentication
   handler
   {:authfn (fn [request]
              (let [auth-header (get-in request [:headers "authorization"])]
                (when auth-header
                  (jwt/verify-token (second (s/split auth-header #" "))))))}))

;; Restrict access based on roles
(defn wrap-authorization [handler required-roles]
  (fn [request]
    (let [user (auth/current-authentication)]
      (if (some #(contains? (set (:roles user)) %) required-roles)
        (handler request)
        {:status 403
         :body {:error "Forbidden"}})))

;; Middleware to enforce role-based access
(defn wrap-role-authorization [handler required-roles]
  (fn [request]
    (let [user (auth/current-authentication)
          user-roles (set (:roles user))]
      (if (not-empty (set/intersection user-roles (set required-roles)))
        (handler request)
        {:status 403
         :body {:error "Forbidden: Insufficient permissions"}})))

;; Middleware to enforce ABAC-based access
(defn wrap-abac-authorization [handler policies]
  (fn [request]
    (let [user (auth/current-authentication) ;; Extract authenticated user
          resource (:resource request)      ;; Extract resource info (custom)
          context {:ip (:remote-addr request)
                   :time (java.time.Instant/now)}] ;; Add context attributes
      (if (abac/evaluate-policy user resource context policies)
        (handler request)
        {:status 403
         :body {:error "Forbidden: Access denied by ABAC policy"}})))

;; Middleware to check required scopes
(defn wrap-scope-authorization [handler required-scopes]
  (fn [request]
    (let [token (get-in request [:headers "authorization"]) ;; Extract token
          scopes (when token (jwt/get-scopes (subs token 7)))] ;; Remove "Bearer " prefix
      (if (and scopes (every? (set scopes) required-scopes))
        (handler request)
        {:status 403
         :body {:error "Forbidden: Insufficient scopes"}})))

(def scope-map
  {"/users" ["read:users"]
   "/posts" ["write:posts"]})

(defn dynamic-scope-authorization [handler]
  (fn [request]
    (let [path (:uri request)
          required-scopes (get scope-map path)]
      ((wrap-scope-authorization handler required-scopes) request))))
