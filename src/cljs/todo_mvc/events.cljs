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
    (.log js/console "let's get our todos from uri" uri)
    {:http-xhrio (build-get-all-todos-request uri)
     :db db}))

(re-frame/reg-event-fx
  :process-todos-list
  (fn [{:keys [db]} [_ todo-data]]
    (.log js/console (str "todo data = " todo-data))
    (swap! db assoc :todos todo-data)
    {:db db}))
