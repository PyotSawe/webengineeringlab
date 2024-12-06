(ns jaminlab.domain.services.users
  (:require [jaminlab.domain.model.entities :as entities]
            [jaminlab.domain.model.repository :as repo]
            [clojure.spec.alpha :as s]
            [jaminlab.domain.model.schemas :as schemas]
            [clojure.string :as str]))

;; Create a user (with validation and saving to DB)
(defn create-user
  "Creates a user in the system, validates data, and saves it to the database."
  [user-data]
  (let [user (->entities/User (:user-id user-data)
                              (:username user-data)
                              (:email user-data)
                              (:password user-data)
                              (:roles user-data))]
    (if (s/valid? ::schemas/user-db user)
      (do
        (repo/insert-user! user) ;; Save to DB (via repository)
        {:status 201
         :body {:message "User created successfully"
                :user user}})
      {:status 400
       :body {:error "Invalid user data"}})))

;; Fetch a user by username (using the repository)
(defn get-user-by-username
  "Fetches a user by their username from the repository."
  [username]
  (let [user (repo/get-user-by-username {:username username})]
    (if (s/valid? ::schemas/user-db user)
      {:status 200 :body user}
      {:status 404 :body {:error "User not found"}})))

;; Fetch orders associated with a user
(defn get-user-orders
  "Fetches all orders associated with a given user."
  [user-id]
  (let [orders (repo/all-orders)]
    (let [user-orders (filter #(= user-id (:user-id %)) orders)]
      (if (seq user-orders)
        {:status 200 :body user-orders}
        {:status 404 :body {:error "No orders found for user"}}))))

;; Other service functions (e.g., update-user, delete-user) can go here.
