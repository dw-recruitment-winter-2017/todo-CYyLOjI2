(ns todo-mvc.events
    (:require [re-frame.core :as re-frame]
              [todo-mvc.db :as db]
              [day8.re-frame.http-fx]
              [ajax.core :as ajax]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(defn build-get-all-todos-request [uri]
  (if (nil? uri)
    nil
    { :method :get
      :uri uri
      :response-format (ajax/json-response-format)
      :on-success [:process-todos-list]}))

(re-frame/reg-event-fx
  :load-todo-data
  (fn [{:keys [db]} [_ uri]]
    {:http-xhrio (build-get-all-todos-request uri)
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

(re-frame/reg-event-db
  :toggle-completed
  (fn [db [_ todo-id]]
    (let [todos (:todos @db)
          todo (first (filter #(= (get % "id") todo-id) todos))
          updated-todo (assoc todo "complete" (not (get todo "complete")))
          updated-todos (map (fn [todo] (if (= (get todo "id") todo-id) updated-todo todo)) todos)]
    (swap! db assoc :todos updated-todos)
    db)))
