(ns todo-mvc.views
    (:require [re-frame.core :refer [subscribe dispatch]]))

(defn completion-filter [display-completed-only]
  [:div "Display "
    [:label [:input {:type "radio" :name "display" :checked (not display-completed-only) :on-change #(dispatch [:show-all-todos])}] "All"]
    [:label [:input {:type "radio" :name "display" :checked display-completed-only :on-change #(dispatch [:show-completed-tods-only])}] "Completed Only"]])

(defn todos-element [todo]
  [:li {:key (get todo "id")}
    (get todo "description")
    [:input {:type "checkbox" :checked (get todo "complete") ::on-change #(dispatch [:toggle-completed (get todo "id")])}]])

(defn todos-list [todos display-completed-only]
  (let [filter-func (if display-completed-only #(get % "complete") identity)
        filtered-todos (filter filter-func todos)
        todo-elements (map todos-element filtered-todos)]
    [:ul todo-elements]))

(defn main-panel []
  (let [todos (subscribe [:todos])
        display-completed-only (subscribe [:completed-only])]
    (fn []
      [:div
        [:h3 "To Do's"]
        (completion-filter @display-completed-only)
        (todos-list @todos @display-completed-only)])))
