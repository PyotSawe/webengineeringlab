(ns jaminlab.domain.security.handlers
  (:require
   [jaminlab.domain.security.vaults :as vaults]
   [jaminlab.domain.security.middlewares :as middlewares]
   [buddy.auth :as auth]
   [clojure.tools.logging :as log]))

(defn login-handler [request]
  (let [user-id (get-in request [:params :user-id])
        password (get-in request [:params :password])
        login-result (vaults/check-password user-id password)]
    (if login-result
      (let [token (auth/generate-token {:user-id user-id})] ;; Generate JWT token after successful login
        (log/info (str "User " user-id " logged in successfully"))
        {:status 200
         :body {:message "Login successful"
                :token token}})
      (do
        (vaults/log-failed-login user-id)
        {:status 401
         :body {:error "Invalid credentials"}}))))

;; Combine middlewares for login
(defn login-with-middleware [request]
  (-> (fn [req] (login-handler req))   ;; Main login handler
      (middlewares/rate-limit)         ;; Rate limiting middleware
      (middlewares/dynamic-scope-authorization))) ;; Optional: Apply dynamic scope authorization


; Login handler
(defn login-handler1 [request]
  (let [user-id (get-in request [:body :user-id])    ; Retrieve user ID from body
        password (get-in request [:body :password])    ; Retrieve password from body
        login-result (vaults/check-password user-id password)]
    (if login-result
      (let [token (auth/generate-token {:user-id user-id})] ;; Generate JWT token after successful login
        (log/info (str "User " user-id " logged in successfully"))
        {:status 200
         :body {:message "Login successful"
                :token token}})
      (do
        (vaults/log-failed-login user-id)
        {:status 401
         :body {:error "Invalid credentials"}}))))

;; Combine middlewares for login
(defn login-with-middleware1 [request]
  (-> (fn [req] (login-handler req))   ;; Main login handler
      (middlewares/rate-limit)         ;; Rate limiting middleware
      (middlewares/dynamic-scope-authorization))) ;; Optional: Apply dynamic scope authorization
