(ns jaminlab.domain.model.aggregates
  (:require [jaminlab.model.entities :as entities]))

(defn add-item-to-order [order item price]
  (let [new-item {:item item :price price}
        updated-items (conj (:items order) new-item)
        updated-total-price (+ (:total-price order) price)]
    (assoc order :items updated-items :total-price updated-total-price)))

(defn create-order-aggregate [user-id items]
  (let [total-price (reduce + (map :price items))
        order (entities/create-order user-id items total-price)]
    {:order order}))

;; Adding order status and discount functionality

(def order-statuses #{:pending :paid :shipped :delivered :cancelled})

(defn create-order-aggregate [user-id items]
  (let [total-price (reduce + (map :price items))
        order (entities/create-order user-id items total-price)]
    {:order order
     :status :pending
     :discount 0
     :total-price total-price}))

(defn apply-discount [order discount-percentage]
  (let [discounted-price (* (:total-price order) (- 1 discount-percentage))]
    (assoc order :total-price discounted-price :discount discount-percentage)))

(defn change-order-status [order new-status]
  (if (order-statuses new-status)
    (assoc order :status new-status)
    (throw (Exception. "Invalid order status"))))

(defn update-order [order action & args]
  (cond
    (= action :apply-discount) (apply-discount order (first args))
    (= action :change-status) (change-order-status order (first args))
    :else (throw (Exception. "Unknown action"))))