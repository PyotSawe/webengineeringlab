(ns jaminlab.domain.cqrs.query
  (:require [jaminlab.domain.model.repository :as repo]))

(defn get-order-details
  "retrieves an order by its ID"
  [order-id]
  (let [order (repo/get-order order-id)]
    (if order
      {:status :success :order order}
      {:status :error :message "Order not found"})))

(defn get-all-orders
  "returns all orders in the system"
  []
  (let [orders (repo/all-orders)]
    {:status :success :orders orders}))