(ns jaminlab.domain.web.routes
  (:require [jaminlab.domain.web.handlers :as handlers]
            [jaminlab.domain.security.middlewares :as middlewares]
            [jaminlab.domain.policies.policies :as policies]
            [jaminlab.domain.policies.schemas :as schemas]
            [jaminlab.domain.policies.validations :as validations]))


(def routes
  [["/public"
    {:get {:handler (fn [_] {:status 200 :body "Public endpoint"})}}]
   ;;Secured-protectd routes
   ["/secured"
    {:get {:middleware [(middlewares/wrap-jwt-auth)]
           :handler handlers/secured-handler}}]
   ;;Role-based secured routes
   ["/admin"
    {:get {:middleware [(middlewares/wrap-role-authorization ["admin"])]
           :handler handlers/admin-handler}}]
   ["/editor"
    {:get {:middleware [(middlewares/wrap-role-authorization ["editor"])]
           :handler handlers/editor-handler}}]
   ;;Policy-based secured routes
   ["/secured-resource"
    {:get {:middleware [(middlewares/wrap-abac-authorization policies/policies)]
           :handler handlers/secured-resource-handler}}]
   ;;Scope-based secured routes
   ["/users"
    {:get {:middleware [(middlewares/wrap-scope-authorization ["read:users"])]
           :handler handlers/read-users-handler}}]
   ["/posts"
    {:post {:middleware [(middlewares/wrap-scope-authorization ["write:posts"])]
            :handler handlers/write-posts-handler}}]
   ;;for complex url to be conformed to schema
   ["/search"
    {:get {:middleware [[validate-request search-schema]]
           :handler handlers/search-handler}}]

   ["/users/:user-id/posts"
    {:get {:handler handlers/user-posts-handler}}]])