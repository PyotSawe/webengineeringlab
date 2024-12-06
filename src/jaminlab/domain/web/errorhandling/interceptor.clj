(ns jaminlab.domain.web.errorhandling.interceptor)

(def error-interceptor
  {:name ::error-handler
   :error (fn [{:keys [request] :as context} ex]
            {:status 500
             :body {:error "Internal Server Error"
                    :message (.getMessage ex)}})})