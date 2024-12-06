(ns jaminlab.domain.web.util)

;; Utility for parsing nested query parameters
(defn parse-nested-query [query-params]
  (reduce-kv (fn [acc k v]
               (if (re-matches #"filters\[(.+?)\]" k)
                 (let [key (second (re-matches #"filters\[(.+?)\]" k))]
                   (assoc-in acc [:filters key] (Boolean/valueOf v)))
                 (assoc acc k v)))
             {}
             query-params))