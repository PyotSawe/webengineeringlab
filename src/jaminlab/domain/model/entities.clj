(ns jaminlab.domain.model.entities
  (:require [clojure.spec.alpha :as s]))

;; Spec to validate a user
(s/def ::user-id uuid? )
(s/def ::user-name string?)
(s/def ::user-email string?)



(defn create-user [name email]
  {:id  (random-uuid)
   :name name
   :email email})

(defn create-order [user-id items total-price]
  {:id (random-uuid)
   :user-id user-id
   :items items
   :total-price total-price})

;; Validation
(defn valid-user? [user]
  (s/valid? ::user-id (:id user)
            ::user-name (:name user)
            ::user-email (:email user)))