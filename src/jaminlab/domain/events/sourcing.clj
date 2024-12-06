(ns jaminlab.domain.events.sourcing
  (:require [jaminlab.domain.events.orders :as order-events]))

;To take CQRS further, we could also implement **Event Sourcing**,
;where every state change is recorded as an event.
;This allows you to rebuild state from a series of events and provides an audit trail.

(defn order-created [order]
  (order-events/order-created-event order))

(defn order-status-changed [order]
  (order-events/order-item-added-event order))


(comment "You would persist these events in an event store (e.g., a database or message queue)
          and reconstruct the state of your aggregates by replaying the events")