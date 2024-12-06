(ns jaminlab.domain.services.users
  (:require [jaminlab.domain.model.entities :as entities]
            [jaminlab.domain.model.repository :as repo]))


(defn create-user [name email]
  (entities/create-user ))

(defn get-user-orders [user-id]
  (filter #(= user-id (:user-id %)) (repo/all-orders)))