(ns todo-mvc.views
    (:require [re-frame.core :refer [subscribe dispatch]]))

(defn completion-filter [display-completed-only]
  [:div "Display "
    [:label [:input {:type "radio" :name "display" :checked (not display-completed-only) :on-change #(dispatch [:show-all-todos])}] "All"]
    [:label [:input {:type "radio" :name "display" :checked display-completed-only :on-change #(dispatch [:show-completed-tods-only])}] "Completed Only"]])

(defn todos-element [todo]
  [:tr {:key (get todo "id")}
    [:td (get todo "description")]
    [:td
      [:input {:type "checkbox"
               :checked (get todo "complete")
               :on-change #(dispatch [:toggle-completed (get todo "id")])}]]])

(defn new-todo [new-todo-description]
  [:tr {:key "new-line"}
    [:td [:input {:type "text"
                  :value new-todo-description
                  :on-change #(dispatch [:update-new-todo-description (-> % .-target .-value)])}]]
    [:td [:input {:type "button"
                  :value "+"
                  :on-click #(dispatch [:add-new-todo])}]]])

(defn todos-list [todos display-completed-only new-todo-description]
  (let [filter-func (if display-completed-only #(get % "complete") identity)
        filtered-todos (filter filter-func todos)
        todo-elements (map todos-element filtered-todos)]
    [:table [:tbody todo-elements (new-todo new-todo-description)]]))

(defn main-panel []
  (let [todos (subscribe [:todos])
        display-completed-only (subscribe [:completed-only])
        new-todo-description (subscribe [:new-todo-description])]
    (fn []
      [:div
        [:h3 "To Do's"]
        (completion-filter @display-completed-only)
        (todos-list @todos @display-completed-only @new-todo-description)])))
