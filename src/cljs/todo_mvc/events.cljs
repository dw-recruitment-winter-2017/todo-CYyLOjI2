(ns todo-mvc.events
    (:require [re-frame.core :as re-frame]
              [todo-mvc.db :as db]
              [day8.re-frame.http-fx]
              [ajax.core :as ajax]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/reg-event-fx
  :load-todo-data
  (fn [{:keys [db]} [_]]
    {:http-xhrio { :method :get
                   :uri "http://localhost:3000/api/todos"
                   :response-format (ajax/json-response-format)
                   :on-success [:process-todos-list]}
     :db db}))

(re-frame/reg-event-fx
  :process-todos-list
  (fn [{:keys [db]} [_ todo-data]]
    (swap! db assoc :todos todo-data)
    {:db db}))

(re-frame/reg-event-db
  :show-all-todos
  (fn [db _]
    (swap! db assoc :completed-only false)
    db))

(re-frame/reg-event-db
  :show-completed-tods-only
  (fn [db _]
    (swap! db assoc :completed-only true)
    db))

(re-frame/reg-event-fx
  :toggle-completed
  (fn [{:keys [db]} [_ todo-id]]
    (let [todos (:todos @db)
          todo (first (filter #(= (get % "id") todo-id) todos))
          is-complete (get todo "complete")
          updated-todo (assoc todo "complete" (not is-complete))
          updated-todos (map (fn [todo] (if (= (get todo "id") todo-id) updated-todo todo)) todos)
          server-action (if is-complete "incomplete" "complete")] ; this is the action we want to perform on the server
    (swap! db assoc :todos updated-todos)
    {:db db
     :dispatch [:update-todo-completion-status todo-id server-action]})))

(re-frame/reg-event-fx
  :update-todo-completion-status
  (fn [{:keys [db]} [_ todo-id server-action]]
    {:http-xhrio { :method :put
                   :uri (str "http://localhost:3000/api/todos/" todo-id "/" server-action)
                   :format (ajax/json-request-format)
                   :response-format (ajax/json-response-format)
                   :on-success [:standard-server-success]}
     :db db}))

(re-frame/reg-event-fx
  :standard-server-success
  (fn [{:keys [db]}]
  {:db db})) ; do nothing for now

(re-frame/reg-event-db
  :update-new-todo-description
  (fn [db [_ description]]
    (swap! db assoc :new-todo-description description)
    db))

(re-frame/reg-event-db
  :add-new-todo
  (fn [db _]
    (let [new-todo {"id" 9000 "description" (:new-todo-description @db) "completed" false}
          updated-todos (conj (:todos @db) new-todo)]
    (.log js/console (str "updated todos " updated-todos " new todo description " (:new-todo-description @db)))
    (swap! db assoc :todos updated-todos :new-todo-description "")
    (.log js/console (str "updated db " @db))
    db)))
