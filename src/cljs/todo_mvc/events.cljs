(ns todo-mvc.events
    (:require [re-frame.core :as re-frame]
              [todo-mvc.db :as db]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))
