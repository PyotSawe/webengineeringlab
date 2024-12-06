(ns jaminlab.domain.model.repository)

;; Db
(defonce orders (atom {}))

(defn save-order [order]
  (swap! orders assoc (:id order) order))

(defn get-order [id]
  (@orders id))

(defn all-orders []
  (vals @orders))