(ns jaminlab.domain.security.authors
  (:require [buddy.sign.jwt :as jwt]
            [jaminlab.security.vault :as vault]))

;; Role-Based Authorization

;; Generate a token for a user
(defn generate-token [user]
  (jwt/sign {:user-id (:id user)
             :roles (:roles user)} ;; Include roles in the token payload
             vault/secret-key))

;; Verify and decode a JWT token
(defn verify-token [token]
  (try
    (jwt/unsign token vault/secret-key)
    (catch Exception e
      (println "Invalid token" e)
      nil)))