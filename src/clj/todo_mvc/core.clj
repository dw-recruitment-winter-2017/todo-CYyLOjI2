(ns todo-mvc.core
  (:require [environ.core :refer [env]]
            [clojure.java.jdbc :as jdbc]
            [todo-mvc.dataaccess :as da]
            [clojure.data.json :as json]))

(defn json-response [status-code body]
  (println "status-code " status-code " body " body)
  {:status status-code
   :headers {"Content-Type" "application/json"}
   :body (json/write-str body)})

(defn with-json [handler content]
  (try
    (let [json-content (json/read-str content)]
      (handler json-content))
    (catch Exception e (json-response 400 {:errors ["Request body must be valid json"]}))))

(defn get-all-todos []
  (let [all-todos (da/get-all-todos da/db-info)
        updated-all-todos (map #(update-in % [:complete] = 1) all-todos)]
  (json/write-str updated-all-todos)))

(defn insert-new-todo [request-body]
  (let [do-insert (fn [todo-content]
      (if (contains? todo-content "description")
        (json-response 200 (da/insert-new-todo! da/db-info (get request-body "description")))
        (json-response 400 {:errors ["Request must contain 'description' key"]}))
    )]
    (with-json do-insert request-body)))
