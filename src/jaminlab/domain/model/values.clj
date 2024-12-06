(ns jaminlab.domain.model.values)


(s/def ::currency string?)
(s/def ::amount number?)

(defn create-money [amount currency]
  {:amount amount :currency currency})

(defn add-money [money1 money2]
  (if (= (:currency money1) (:currency money2))
    (create-money (+ (:amount money1) (:amount money2)) (:currency money1))
    (throw (Exception. "Currencies must be the same"))))