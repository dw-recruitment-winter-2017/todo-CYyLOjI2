(ns todo-mvc.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
 :todos
 (fn [db _]
   (:todos @db)))

(reg-sub
  :completed-only
  (fn [db _]
    (:completed-only @db)))
