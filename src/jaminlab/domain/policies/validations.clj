(ns jaminlab.domain.policies.validations
  (:require [malli.core :as m]))

(defn validate-order-total-price [order]
  (if (< (:total-price order) 10)
    (throw (Exception. "Order total must be at least $10"))
    true))

(defn validate-sufficient-balance [user amount]
  (let [balance (:balance user)]
    (if (< balance amount)
      (throw (Exception. "Insufficient balance"))
      true)))

(defn validate-order [order user]
  (and (validate-order-total-price order)
       (every? #(validate-sufficient-balance user (:price %)) (:items order))))


;;Implement company policies of validations and sanity of data
(defn validate-request
  [schema handler]
  (fn [request]
    (let [params (:query-params request)
          result (m/validate schema params)]
      (if result
        (handler request)
        {:status 400
         :body {:error "Invalid request parameters"}}))))