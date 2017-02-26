(ns todo-mvc.core
  (:require [environ.core :refer [env]]
            [clojure.java.jdbc :as jdbc]
            [todo-mvc.dataaccess :as da]
            [clojure.data.json :as json]))

; utilities
(defn json-response [status-code body]
  (println "status-code " status-code " body " body)
  {:status status-code
   :headers {"Content-Type" "application/json"
             }
   :body (json/write-str body)})

(defn with-json [handler content]
  (try
    (let [json-content (json/read-str content)]
      (handler json-content))
    (catch Exception e (json-response 400 {:errors ["Request body must be valid json"]}))))

(defn build-serializable-todo [todo]
  (update-in todo [:complete] = 1))

(defn save-marking [marking-function todo-id]
  (marking-function da/db-info todo-id)
  (build-serializable-todo (da/get-todo da/db-info todo-id)))

(defn with-todo-existence-check [handler-function todo-id]
  (let [original-todo (da/get-todo da/db-info todo-id)
        error-msg (str "Could not find todo with id " todo-id)]
    (if (some? original-todo)
      (json-response 200 (handler-function todo-id))
      (json-response 404 {:errors [error-msg]}))))

; get /
(defn get-all-todos []
  (let [all-todos (da/get-all-todos da/db-info)
        updated-all-todos (map build-serializable-todo all-todos)]
  (json/write-str updated-all-todos)))

; post /
(defn save-new-todo-and-return [description]
  (let [insert-response (da/insert-new-todo! da/db-info description)
        todo-id (get (first insert-response) :generated_key)]
        (build-serializable-todo (da/get-todo da/db-info todo-id))))

(defn do-insert [todo-content]
  (if (contains? todo-content "description")
    (json-response 200 (save-new-todo-and-return (get todo-content "description")))
    (json-response 400 {:errors ["Request must contain 'description' key"]})))

(defn insert-new-todo [request-body]
  (with-json do-insert request-body))

; put /:todo-id/complete
(defn mark-complete [todo-id]
  (with-todo-existence-check (partial save-marking da/mark-complete!) todo-id))

; put /:todo-id/incomplete
(defn mark-incomplete [todo-id]
  (with-todo-existence-check (partial save-marking da/mark-incomplete!) todo-id))

; delete /:todo-id
(defn delete-todo-and-notify [todo-id]
  (da/delete-todo! da/db-info todo-id)
  {:completed true})

(defn delete-todo [todo-id]
  (with-todo-existence-check delete-todo-and-notify todo-id))
