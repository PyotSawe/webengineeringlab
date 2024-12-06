(ns jaminlab.domain.services.orders)

(defn create-order [user-id items]
  (let [order-aggregate (order-agg/create-order-aggregate user-id items)]
    (order-repo/save-order (:order order-aggregate))
    (:order order-aggregate)))

(defn add-item-to-order [order-id item price]
  (let [order (order-repo/get-order order-id)]
    (if order
      (order-repo/save-order (order-agg/add-item-to-order order item price))
      (throw (Exception. "Order not found")))))