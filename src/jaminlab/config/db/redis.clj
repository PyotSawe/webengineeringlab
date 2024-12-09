(ns your-app.redis
  (:require [taoensso.carmine :as car]))

(def redis-conn
  {:pool {}
   :spec {:host "127.0.0.1" :port 6379}})
   
(defmacro wcar* [& body]
  `(car/wcar redis-conn ~@body))
