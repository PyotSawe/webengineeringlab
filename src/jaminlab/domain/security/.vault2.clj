(ns jaminlab.domain.security.vaults
  (:require
   [buddy.hashers :as hashers]
   [buddy.core.crypto :as crypto]
   [clojure.java.jdbc :as jdbc]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [clojure.set :as set]))

;; --- Database Configuration ---
(def db-spec
  {:dbtype "postgresql"   ;; Example: You can change this to MySQL or any other DB type.
   :host "localhost"
   :port 5432
   :dbname "passwords_db"
   :user "dbuser"
   :password "dbpassword"
   :ssl true})  ;; Use SSL to connect to the database for security

;; --- Secure Password Handling ---
(def post-quantum-hash-function :sha3-512) ;; Placeholder for post-quantum hash function, replace with real post-quantum algorithms when available

(defn hash-password [plain-password]
  (let [salt (crypto/rand-bytes 16)] ;; Generate a unique salt for each password
    (hashers/derive plain-password {:salt salt :iterations 1000000 :algorithm post-quantum-hash-function})))

(defn verify-password [plain-password hashed-password]
  (hashers/check plain-password hashed-password))

;; --- Store Password in the Database ---
(defn store-password-in-db [user-id plain-password]
  (let [hashed-password (hash-password plain-password)
        salt (get hashed-password :salt)  ;; Extract the salt from the hashed password
        created-at (java.time.Instant/now)]
    (jdbc/insert! db-spec :user_passwords
                  {:user_id user-id
                   :hashed_password (:password hashed-password)
                   :salt salt
                   :created_at created-at})))

;; --- Retrieve Password from Database ---
(defn retrieve-password-from-db [user-id]
  (first (jdbc/query db-spec
                     ["SELECT * FROM user_passwords WHERE user_id = ?" user-id])))

;; --- Verify Password ---
(defn check-password [user-id plain-password]
  (let [user (retrieve-password-from-db user-id)]
    (if user
      (let [hashed-password (:hashed_password user)
            salt (:salt user)]
        (verify-password plain-password hashed-password))
      (do
        (log/warn "User not found: " user-id)
        false))))

;; --- Encrypt and Decrypt Other Secrets ---
(defn encrypt-data [data]
  ;; Encrypt the data using a symmetric encryption algorithm (AES)
  (crypto/encrypt data "super-secret-key"))

(defn decrypt-data [encrypted-data]
  ;; Decrypt the data using the same encryption algorithm
  (crypto/decrypt encrypted-data "super-secret-key"))

;; --- Storing and Retrieving Secrets ---
(defn store-secret [secret-name secret-value]
  ;; Encrypt and store a secret value in a file or a database
  (let [encrypted-secret (encrypt-data secret-value)]
    (spit (str "secrets/" secret-name ".enc") encrypted-secret)))

(defn retrieve-secret [secret-name]
  ;; Decrypt and retrieve a stored secret
  (let [encrypted-secret (slurp (str "secrets/" secret-name ".enc"))]
    (decrypt-data encrypted-secret)))

;; --- Example Usage ---
;; Hashing password
(def hashed-password (hash-password "my-password"))

;; Verify password
(def password-verified? (verify-password "my-password" hashed-password))

;; Storing password in the database
(store-password-in-db "user123" "my-password")

;; Retrieve password from the database and check if it's valid
(def password-valid? (check-password "user123" "my-password"))

;; Secret storage and retrieval
(store-secret "api-key" "super-secret-api-key")
(def api-key (retrieve-secret "api-key"))
