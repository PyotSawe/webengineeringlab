
(def db-config (config/get-db-config))

(def datasource (jdbc/get-datasource db-config))

(defn get-conn []
  (jdbc/get-connection datasource))