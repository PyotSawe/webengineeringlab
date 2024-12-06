(ns jaminlab.domain.security.jwt
    (:require
     [buddy.sign.jwt :as jwt]
     [jaminlab.domain.security.vaults :as vault]))
;; Generate a JWT token for a user
(defn generate-token [user]
  (jwt/sign {:user-id (:id user)
             :roles (:roles user)}
            vault/secret-key))

;; Verify and decode a JWT token
(defn verify-token [token]
  (try
    (jwt/unsign token vault/secret-key)
    (catch Exception e
      (println "Invalid token" e)
      nil)))
;; Extract scopes from the token
(defn get-scopes [token]
  (when-let [decoded (verify-token token)]
    (:scopes decoded)))