(ns todo-mvc.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [resource-response]]
            [ring.middleware.reload :refer [wrap-reload]]))

(defroutes routes
  (GET "/" [] (resource-response "index.html" {:root "public"}))
  (resources "/"))

; (defroutes api-routes
;   (context "/api/todos" []
;     (GET "/" [] (get-all-todos))
;     (PUT "/" [] (insert-new-todo))
;     (PUT "/:todo-id" [todo-id] (update-todo todo-id))
;     (PUT "/:todo-id/complete" [todo-id] (mark-complete todo-id))
;     (PUT "/:todo-id/incomplete" [todo-id] (mark-incomplete todo-id))
;     (DELETE "/:todo-id" [todo-id] (delete-todo todo-id))
;   ))

(def dev-handler (-> #'routes wrap-reload))

(def handler routes)
