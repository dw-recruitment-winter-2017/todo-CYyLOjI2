(ns todo-mvc.server
  (:require [todo-mvc.handler :refer [handler]]
            [config.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

 (defn -main [& args]
   (let [port (Integer/parseInt (or (env :port) "3000"))]
     (run-jetty handler {:port port :join? false})))
