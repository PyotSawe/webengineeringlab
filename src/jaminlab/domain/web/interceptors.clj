(ns jaminlab.domain.web.interceptors
  (:require [io.pedestal.http.cors :as cors]))

;; Secure cors

(def cors-allow-all
  (cors/cors-interceptor
   {:allow-origin "*"
    :allow-methods [:get :post :put :delete]
    :allow-headers ["Authorization" "Content-Type"]}))