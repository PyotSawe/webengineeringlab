(ns jaminlab.domain.web.handlers
  (:require [jaminlab.domain.web.util :as util]))

(defn admin-handler [request]
  {:status 200
   :body {:message "Welcome, admin user"}})

(defn editor-handler [request]
  {:status 200
   :body {:message "Welcome, editor user"}})

(defn secured-handler [request]
  {:status 200
   :body {:message "Welcome to the secured endpoint"}})

(defn secured-resource-handler [request]
  {:status 200
   :body {:message "Access granted to secured resource"}})

(defn read-users-handler [request]
  {:status 200
   :body {:message "Access granted to read users"}})

(defn write-posts-handler [request]
  {:status 200
   :body {:message "Access granted to write posts"}})


;; Search handler
(defn search-handler
  "For: https://x.com/search?q=space%20x
                             &src=typed_query
                             &f=media"
  [request]
  (let [query (get-in request [:query-params "q"])
        source (get-in request [:query-params "src"])
        filter (get-in request [:query-params "f"])]
    {:status 200
     :body {:query query
            :source source
            :filter filter}}))

;;Search handler that process nested queries
(defn search-handler2
  [request]
  (let [query (get-in request [:query-params "q"] "default-query")
        source (get-in request [:query-params "src"] "default-source")
        filters (util/parse-nested-query (:query-params request))]
    {:status 200
     :body {:query query
            :source source
            :filters filters}}))


(defn user-posts-handler
  "`/users/:user-id/posts`: The `:user-id` segment is dynamic, allowing specific values like `/users/123/posts`.
   - Accessed via `[:path-params :user-id]`."
  [request]
  (let [user-id (get-in request [:path-params :user-id])
        limit (Integer/parseInt (get-in request [:query-params "limit"] "10"))
        page (Integer/parseInt (get-in request [:query-params "page"] "1"))]
    {:status 200
     :body {:user-id user-id
            :limit limit
            :page page}}))