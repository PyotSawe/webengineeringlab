(ns jaminlab.domain.security.vaults
  (:require [buddy.hashers :as hashers]))

;; Secure password handling using Buddy's `buddy.hashers `.
(def secret-key "my-super-secret-key")
;; Hash a password before storing it in the database
(defn hash-password [plain-password]
  (hashers/derive plain-password))

;; Verify a password against a stored hash
(defn verify-password [plain-password hashed-password]
  (hashers/check plain-password hashed-password))