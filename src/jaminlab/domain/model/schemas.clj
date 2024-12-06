(ns jaminlab.domain.model.schemas)

;; Define the user schema
(s/def ::user-id string?)
(s/def ::username string?)
(s/def ::email string?)
(s/def ::password string?)
(s/def ::roles (s/coll-of string?))

(s/def ::user-db
  (s/keys :req-un [::user-id ::username ::email ::password ::roles]))

;; Define the session schema
(s/def ::session-id string?)
(s/def ::session-token string?)
(s/def ::user-id-ref string?)

(s/def ::session-db
  (s/keys :req-un [::session-id ::session-token ::user-id-ref]))