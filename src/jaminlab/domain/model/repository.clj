(ns jaminlab.domain.model.repository
  (:require [hugsql.core :as hugsql]))

; This is where we use hugsql
;; Db
(defonce orders (atom {}))

;; Define the SQL queries using HugSQL
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

;; Register HugSQL functions
(hugsql/def-db-fns user-queries)

;; Insert a user into the database (after validating using the user schema)
(defn insert-user 
  "Insert a user who conform to schemas defined"
  [db user]
  (if (s/valid? ::schemas/user-db user)
    (insert-user! db user)   ;; HugSQL query call
    (throw (ex-info "Invalid user data" {:user user})))) ;; Validation error if invalid

;; Get a user by username (after validating)
(defn get-user-by-username [db username]
  (let [user (get-user-by-username db {:username username})]  ;; HugSQL query call
    (if (s/valid? ::schemas/user-db user)
      user
      (throw (ex-info "Invalid user data from DB" {:user user})))))

;; Create a session (after validating using the session schema)
(defn create-session [db session]
  (if (s/valid? ::schemas/session-db session)
    (huqsql/create-session! db session)  ;; HugSQL query call
    (throw (ex-info "Invalid session data" {:session session})))) ;; Validation error if invalid


(defn save-order [order]
  (swap! orders assoc (:id order) order))

(defn get-order [id]
  (@orders id))

(defn all-orders []
  (vals @orders))