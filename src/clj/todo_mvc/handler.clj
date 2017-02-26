(ns todo-mvc.handler
  (:require [compojure.core :refer [GET POST PUT DELETE OPTIONS defroutes context routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [resource-response]]
            [ring.middleware.reload :refer [wrap-reload]]
            [todo-mvc.core :as core]))

(defroutes api-routes
  (context "/api/todos" []
    (GET "/" [] (core/get-all-todos))
    (POST "/" {body :body} (core/insert-new-todo (slurp body)))
    (OPTIONS "/" [] (core/stupid-standard-cors-preflight-response ["GET" "POST"]))
    (PUT "/:todo-id/complete" [todo-id] (core/mark-complete todo-id))
    (OPTIONS "/:todo-id/complete" [todo-id] (core/stupid-standard-cors-preflight-response ["PUT"]))
    (PUT "/:todo-id/incomplete" [todo-id] (core/mark-incomplete todo-id))
    (OPTIONS "/:todo-id/incomplete" [todo-id] (core/stupid-standard-cors-preflight-response ["PUT"]))
    (DELETE "/:todo-id" [todo-id] (core/delete-todo todo-id))
    (OPTIONS "/:todo-id" [todo-id] (core/stupid-standard-cors-preflight-response ["DELETE"]))))

(defroutes home-routes
  (GET "/" [] (resource-response "index.html" {:root "public"}))
  (GET "/about" [] (resource-response "about.html" {:root "public"}))
  (resources "/"))

(def dev-handler (-> #'routes wrap-reload))

(def handler (routes home-routes api-routes))
