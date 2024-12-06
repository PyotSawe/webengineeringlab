(ns jaminlab.domain.policies.schemas)

;; Validating Query Parameters
(comment "Well validate:
- `q` (search query): Required, must be a non-empty string.
- `src`: Optional, defaulting to `typed_query`.
- `limit` and `page` (pagination): Must be positive integers.
")

;; Validation Schemas
(def search-schema
  [:map
   [:q {:optional false} string?]
   [:src {:optional true} [:enum "typed_query" "user_query"]]
   [:filters {:optional true} map?]])

(def pagination-schema
  [:map
   [:limit int?]
   [:page int?]])