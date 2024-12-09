(ns jaminlab.domain.model.entities
  (:require [clojure.spec.alpha :as s]
            [jaminlab.domain.model.schemas :as schemas]
            [jaminlab.domain.policies.validations-protocols :as policy-prot]))

;; require schema and validation modules
(s/def ::user-id uuid? )
(s/def ::user-name string?)
(s/def ::user-email string?)

;; Define a User entity using defrecord
(defrecord User [user-id username email password roles]
  ;; Implement entity-specific methods for User if necessary
  ;; Example: A method to validate the user data
  Object
  (toString [this]
    (str "User: " (:username this) " with roles: " (clojure.string/join ", " (:roles this)))))

; All enties here Comply to policies
;; Implement the Policy protocol for User and Sessions
(extend-protocol policy-prot/EntityOperations
  User
  (validate [this]
    (if (s/valid? ::schemas/user-db this)
      this
      (throw (ex-info "Invalid user data" {:user this}))))
  (transform [this]
    ;; Example transformation: remove sensitive fields like password before storage
    (assoc this :password nil))

  ;; Implement for Session
  Session
  (validate [this]
    (if (s/valid? ::schemas/session-db this)
      this
      (throw (ex-info "Invalid session data" {:session this}))))
  (transform [this]
    ;; Example transformation: remove sensitive data from session
    (assoc this :session-token nil)))

;; Define a Session entity using defrecord
(defrecord Session [session-id session-token user-id-ref]
  ;; Implement methods for Session entity
  Object
  (toString [this]
    (str "Session: " (:session-id this) " for user: " (:user-id-ref this))))


(defn create-user [name email]
  {:id  (random-uuid)
   :name name
   :email email})

(defn user [{:as user-details}]
  (map->User user-details))

(defn create-order [user-id items total-price]
  {:id (random-uuid)
   :user-id user-id
   :items items
   :total-price total-price})

;; Validation
(defn valid-user? [user]
  (s/valid? ::user-id (:id user)
            ::user-name (:name user)
            ::user-email (:email user)))