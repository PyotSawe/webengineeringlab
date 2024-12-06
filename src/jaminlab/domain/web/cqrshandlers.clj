(ns jaminlab.domain.web.cqrshandlers
  (:require [jaminlab.domain.cqrs.command :as command]
            [jaminlab.domain.cqrs.query :as query]))

;; To fully integrate CQRS, we can set up separate **Command Handlers** and **Query Handlers**, which will be called by respective routes in the API layer. For example:

(def app-routes
  [["/orders"
    {:get  (fn [_] (query/get-all-orders))
     :post (fn [req] (command/create-order (:user-id req) (:items req)))}]

   ["/orders/{id}"
    {:get  (fn [req] (query/get-order-details (:id req)))
     :put  (fn [req] (command/update-order-status (:id req) (:status req)))}]])