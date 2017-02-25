(ns todo-mvc.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :todos
 (fn [db]
   (:todos @db)))
