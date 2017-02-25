(ns todo-mvc.handler
  (:require [compojure.core :refer [GET POST PUT defroutes context routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [resource-response]]
            [ring.middleware.reload :refer [wrap-reload]]
            [todo-mvc.core :as core]))

(defroutes api-routes
  (context "/api/todos" []
    (GET "/" [] (core/get-all-todos))
    (POST "/" {body :body} (core/insert-new-todo (slurp body)))
;     (PUT "/:todo-id" [todo-id] (core/update-todo todo-id))
     (PUT "/:todo-id/complete" [todo-id] (core/mark-complete todo-id))
     (PUT "/:todo-id/incomplete" [todo-id] (core/mark-incomplete todo-id))
;     (DELETE "/:todo-id" [todo-id] (core/delete-todo todo-id))
    ))

(defroutes home-routes
  (GET "/" [] (resource-response "index.html" {:root "public"}))
  (resources "/"))

(def dev-handler (-> #'routes wrap-reload))

(def handler (routes home-routes api-routes))
