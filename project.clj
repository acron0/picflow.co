(defproject picflow_co "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.1"]
                 [ring/ring-defaults "0.1.2"]
                 [ring.middleware.jsonp "0.1.6"]
                 [ring-cors "0.1.7"]
                 [com.novemberain/monger "2.0.0"]
                 [liberator "0.12.2"]]
  :plugins [[lein-ring "0.8.13"]]
  :ring {:handler picflow_co.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
