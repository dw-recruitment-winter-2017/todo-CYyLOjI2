(ns todo-mvc.views
    (:require [re-frame.core :as re-frame]))

(defn main-panel []
  (let [todos (re-frame/subscribe [:todos])]
    (fn []
      (.log js/console @todos)
      [:div
      [:h3 "To Do's"]
      [:ul
      (map #([:li %]) @todos)]])))
