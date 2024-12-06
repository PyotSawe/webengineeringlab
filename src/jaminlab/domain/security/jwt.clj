(ns jaminlab.domain.security.jwt
  (:require
   [buddy.sign.jwt :as jwt]
   [jaminlab.domain.security.vaults :as vault]
   [java-time :as jt]))

;; Configuration
(def default-expiration-minutes 30) ;; Token expiry duration
(def refresh-expiration-minutes 1440) ;; Refresh token expiry duration (1 day)

;; Generate a JWT token for a user
(defn generate-token
  [user & {:keys [expiration] :or {expiration default-expiration-minutes}}]
  (let [claims {:user-id (:id user)
                :roles (:roles user)
                :scopes (:scopes user)
                :exp (jt/to-millis (jt/plus (jt/instant) (jt/minutes expiration)))}]
    (jwt/sign claims vault/secret-key)))

;; Generate a refresh token
(defn generate-refresh-token [user]
  (generate-token user :expiration refresh-expiration-minutes))

;; Verify and decode a JWT token
(defn verify-token
  [token]
  (try
    (jwt/unsign token vault/secret-key)
    (catch Exception e
      (println "Invalid token:" (.getMessage e))
      nil)))

;; Extract scopes from the token
(defn get-scopes [token]
  (when-let [decoded (verify-token token)]
    (:scopes decoded)))

;; Extract roles from the token
(defn get-roles [token]
  (when-let [decoded (verify-token token)]
    (:roles decoded)))

;; Check if the token is expired
(defn token-expired? [token]
  (when-let [decoded (verify-token token)]
    (let [exp (:exp decoded)
          now (jt/to-millis (jt/instant))]
      (or (nil? exp)
          (< exp now)))))

;; Refresh a token if it's valid but expired
(defn refresh-token [refresh-token]
  (if-let [decoded (verify-token refresh-token)]
    (if-not (token-expired? refresh-token)
      (generate-token decoded)
      (do
        (println "Refresh token expired.")
        nil))
    (do
      (println "Invalid refresh token.")
      nil)))

;; Invalidate a token (usually implemented with a blacklist mechanism)
(defonce token-blacklist (atom #{}))

(defn blacklist-token! [token]
  (when-let [decoded (verify-token token)]
    (swap! token-blacklist conj (:jti decoded))
    true))

(defn token-blacklisted? [token]
  (when-let [decoded (verify-token token)]
    (contains? @token-blacklist (:jti decoded))))

;; Middleware for token verification
(defn wrap-authentication [handler]
  (fn [request]
    (let [auth-header (get-in request [:headers "authorization"])
          token (when (and auth-header (string? auth-header))
                  (last (clojure.string/split auth-header #" ")))
          verified (verify-token token)]
      (if (and verified (not (token-blacklisted? token)))
        (handler (assoc request :user verified))
        {:status 401
         :body "Unauthorized"}))))
