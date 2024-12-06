(ns jaminlab.domain.events.orders)

;; we are publishing domain events like `:order-created` and `:order-item-added`,
; which can be handled by subscribers
; (e.g., sending notifications, triggering external integrations)

(defonce events (atom []))

(defn publish-event [event]
  (swap! events conj event))

(defn order-created-event [order]
  (publish-event {:type :order-created :order order}))

(defn order-item-added-event [order]
  (publish-event {:type :order-item-added :order order}))