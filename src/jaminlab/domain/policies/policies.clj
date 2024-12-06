(ns jaminlab.domain.policies.policies)

(defn ownership-policy [user resource _]
  ;; Allow if the user owns the resource
  (= (:user-id user) (:owner-id resource)))

(defn admin-role-policy [user _ _]
  ;; Allow if the user is an admin
  (some #{"admin"} (:roles user)))

(defn time-restricted-policy [_ _ context]
  ;; Allow access only during working hours (9 AM to 5 PM UTC)
  (let [hour (.getHour (java.time.ZonedDateTime/parse (:time context)))]
    (<= 9 hour 17)))

(def policies [ownership-policy admin-role-policy time-restricted-policy])