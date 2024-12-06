(ns jaminlab.domain.security.middlewares
  (:require
   [buddy.auth :as auth]
   [buddy.auth.middleware :as auth-middleware]
   [clojure.set :as set]
   [clojure.string :as s]
   [jaminlab.domain.security.jwt :as jwt]
   [jaminlab.domain.policies.abac :as abac]))


(defn get-user-roles [request]
  (when-let [user (auth/current-authentication)]
    (:roles user)))

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
         :body {:error "Forbidden"}}))))

;; Middleware to enforce role-based access
(defn wrap-role-authorization [handler required-roles]
  (fn [request]
    (let [user (auth/current-authentication)
          user-roles (set (:roles user))]
      (if (not-empty (set/intersection user-roles (set required-roles)))
        (handler request)
        {:status 403
         :body {:error "Forbidden: Insufficient permissions"}}))))

;;Middleware to enforce abac-based acces
(defn wrap-abac-authorization [handler policies]
  (fn [request]
    (let [user (auth/current-authentication) ;; Extract authenticated user
          resource (:resource request)      ;; Extract resource info (custom)
          context {:ip (:remote-addr request)
                   :time (java.time.Instant/now)}] ;; Add context attributes
      (if (abac/evaluate-policy user resource context policies)
        (handler request)
        {:status 403
         :body {:error "Forbidden: Access denied by ABAC policy"}}))))

;; Middleware to check required scopes
(defn wrap-scope-authorization [handler required-scopes]
  (fn [request]
    (let [token (get-in request [:headers "authorization"]) ;; Extract token
          scopes (when token (jwt/get-scopes (subs token 7)))] ;; Remove "Bearer " prefix
      (if (and scopes (every? (set scopes) required-scopes))
        (handler request)
        {:status 403
         :body {:error "Forbidden: Insufficient scopes"}}))))

(def scope-map
  {"/users" ["read:users"]
   "/posts" ["write:posts"]})


(defn dynamic-scope-authorization [handler]
  (fn [request]
    (let [path (:uri request)
          required-scopes (get scope-map path)]
      ((wrap-scope-authorization handler required-scopes) request))))


(defn wrap-multiple-scope-authorization [handler required-scopes-list]
  (fn [request]
    (let [token (get-in request [:headers "authorization"])
          scopes (when token (jwt/get-scopes (subs token 7)))]
      (if (some #(every? (set scopes) %) required-scopes-list)
        (handler request)
        {:status 403
         :body {:error "Forbidden: Insufficient scopes"}}))))

;; Validator middlewares Apply schemas policies