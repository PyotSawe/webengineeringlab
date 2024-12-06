(ns jaminlab.domain.policies.abac)

;; Example policy
(defn example-policy [user resource context]
  ;; Allow access if the user owns the resource or has the "admin" role
  (or (= (:user-id user) (:owner-id resource))
      (some #{"admin"} (:roles user))))

;; General policy evaluator
(defn evaluate-policy [user resource context policies]
  (some true? (map #(apply % [user resource context]) policies)))


;;EXAMPLES
;; User, resource, and context
(def user {:user-id 1 :roles ["editor"]})
(def resource {:resource-id 100 :owner-id 1})
(def context {:time "2024-12-05T12:00:00Z" :ip "192.168.1.1"})

;; Policies
(def policies [example-policy])

;; Evaluate access
(println "Access granted?" (evaluate-policy user resource context policies))