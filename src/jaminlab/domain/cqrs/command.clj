(ns jaminlab.domain.cqrs.command
  (:require [jaminlab.domain.model.aggregates :as aggre]
            [jaminlab.domain.model.repository :as repo]
            [jaminlab.domain.policies.validations :as validation]))

(defn create-order
  "creates a new order and applies validation before saving it"
  [user-id items]
  (let [order-aggregate (aggre/create-order-aggregate user-id items)]
    (validation/validate-order (:order order-aggregate) user-id)
    (repo/save-order (:order order-aggregate))
    {:status :success :order (:order order-aggregate)}))

(defn update-order-status
  "updates the status of an existing order"
  [order-id new-status]
  (let [order (repo/get-order order-id)]
    (if order
      (do
        (aggre/change-order-status order new-status)
        (repo/save-order order)
        {:status :success :order order})
      {:status :error :message "Order not found"})))