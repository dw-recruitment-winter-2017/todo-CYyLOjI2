(ns todo-mvc.events
    (:require [re-frame.core :as re-frame]
              [todo-mvc.db :as db]
              [day8.re-frame.http-fx]
              [ajax.core :as ajax]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(def base-url (str (-> js.window .-location .-href) "api/todos"))

(defn base-request-map [method uri]
  { :method method
    :uri uri
    :format (ajax/json-request-format)
    :response-format (ajax/json-response-format)
    :on-success [:standard-server-success]
    :on-failure [:standard-server-failure]})

(defn map-to-json [m]
  (.stringify js/JSON (clj->js m)))

(re-frame/reg-event-fx
  :load-todo-data
  (fn [{:keys [db]} [_]]
    {:http-xhrio (assoc (base-request-map :get base-url) :on-success [:process-todos-list])
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
    {:http-xhrio (base-request-map :put (str base-url "/" todo-id "/" server-action))
     :db db}))

(re-frame/reg-event-fx
  :standard-server-success
  (fn [{:keys [db]}]
  {:db db})) ; do nothing for now

(re-frame/reg-event-fx
  :standard-server-failure
  (fn [{:keys [db]}]
  {:db db})) ; do nothing for now

(re-frame/reg-event-db
  :update-new-todo-description
  (fn [db [_ description]]
    (swap! db assoc :new-todo-description description)
    db))

(re-frame/reg-event-fx
  :add-new-todo
  (fn [{:keys [db]} _]
    (let [todos (:todos @db)
          current-description (:new-todo-description @db)
          current-max-id (reduce (fn [curr-max todo] (if (> curr-max (get todo "id")) curr-max (get todo "id"))) todos)
          new-id (inc current-max-id)
          new-todo {"id" new-id "description" (:new-todo-description @db) "completed" false}
          updated-todos (conj todos new-todo)]
    (swap! db assoc :todos updated-todos :new-todo-description "")
    { :db db
      :dispatch [:server-add-new-todo current-description]})))

(re-frame/reg-event-fx
  :server-add-new-todo
  (fn [{:keys [db]} [_ description]]
    { :http-xhrio (assoc (base-request-map :post base-url) :body (map-to-json { :description description }))
      :db db}))

(re-frame/reg-event-fx
  :delete-todo
  (fn [{:keys [db]} [_ todo-id]]
    (.log js/console "deleting the todo with id " todo-id)
    (let [updated-todos (filter #(not= (get % "id") todo-id) (:todos @db))]
      (swap! db assoc :todos updated-todos)
      { :db db
        :dispatch [:server-delete-todo todo-id]})))

(re-frame/reg-event-fx
  :server-delete-todo
  (fn [{:keys [db]} [_ todo-id]]
    { :http-xhrio (base-request-map :delete (str base-url "/" todo-id))
      :db db }))
