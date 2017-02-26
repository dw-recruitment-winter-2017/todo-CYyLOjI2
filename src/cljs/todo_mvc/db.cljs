(ns todo-mvc.db
  (:require [reagent.core :as reagent]))

(def default-db
  (reagent/atom {:todos []
                 :completed-only false}))
