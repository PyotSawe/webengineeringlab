(ns jaminlab.domain.policies.validations-protocols)
; This defines all behaviours or interface that all entites to have
(defprotocol EntityOperations
  (validate [this] "Validate the entity data based on its schema.")
  (transform [this] "Transform the entity data for storage or other purposes."))