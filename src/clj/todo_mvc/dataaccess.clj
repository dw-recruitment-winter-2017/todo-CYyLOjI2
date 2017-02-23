(ns todo-mvc.dataaccess
  (:require [environ.core :refer [env]]
            [clojure.java.jdbc :as jdbc]))

(def db-info {
  :dbtype (env :dbtype)
  :host (env :dbhost)
  :dbname (env :dbname)
  :user (env :dbuser)
  :password (env :dbpw)
  })

(defn create-tables! [db]
  (jdbc/db-do-commands db
    (jdbc/create-table-ddl :todos
      [[:id :integer "PRIMARY KEY" "AUTO_INCREMENT"]
       [:description :text]
       [:complete :integer "DEFAULT 0"]
       [:added :timestamp]
      ]))
  )

(defn get-all-todos [db]
  (jdbc/query db ["select id, description, complete from todos order by added"]))

(defn get-todo [db todo-id]
  (first (jdbc/query db ["select id, description, complete from todos where id = ?" todo-id])))

(defn insert-new-todo! [db description]
  (jdbc/insert! db :todos {:description description :complete 0}))

(defn update-todo! [db todo-id description]
  (jdbc/update! :todos {:description description} ["id = ?" todo-id]))

(defn mark-complete! [db todo-id]
  (jdbc/update! :todos {:complete 1} ["id = ?" todo-id]))

(defn mark-incomplete! [db todo-id]
  (jdbc/update! :todos {:complete 0} ["id = ?" todo-id]))

(defn delete-todo! [db todo-id]
  (jdbc/delete! :todos ["id = ?" todo-id]))
