(ns jaminlab.domain.specs.entityspecs)

;; Spec for User entity
(s/def ::user-id uuid?)
(s/def ::user-name string?)
(s/def ::user-email string?)

;; Spec for Money value object
(s/def ::currency string?)
(s/def ::amount number?)

;; Spec for Order entity
(s/def ::order-id uuid?)
(s/def ::order-items (s/coll-of map?))
(s/def ::order-total-price number?)

(s/def ::valid-order (s/keys :req-un [::order-id ::order-items ::order-total-price]))