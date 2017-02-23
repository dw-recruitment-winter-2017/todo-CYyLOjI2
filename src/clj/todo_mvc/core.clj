(ns todo-mvc.core
  (:require [environ.core :refer [env]]
            [clojure.java.jdbc :as jdbc]
            [todo-mvc.dataaccess :as da]
            [clojure.data.json :as json]))

(defn get-all-todos []
  (let [all-todos (da/get-all-todos da/db-info)
        updated-all-todos (map #(update-in % [:complete] = 1) all-todos)]
  (json/write-str updated-all-todos)))
