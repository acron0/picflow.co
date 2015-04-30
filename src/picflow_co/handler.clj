(ns picflow_co.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.db :as mdb]
            [monger.query :as mq]
            [clojure.data.json :as json])
  (:use [liberator.core :only [defresource]]
        [ring.middleware.params :only [wrap-params]]))

;; connection
(def mg-conn (mg/connect))
(def mg-db (mg/get-db mg-conn "images"))

(defn get-albums
  []
  (vec (remove #(= % "system.indexes") (mdb/get-collection-names mg-db))))

(defn get-random-image
  ([]
   (let [album (rand-nth (get-albums))]
     (get-random-image album)))
  ([album]
     (dissoc (first (mq/with-collection mg-db album
      (mq/find {})
      (mq/fields [:src :desc])
      (mq/limit 1)
      (mq/skip (rand-int (mc/count mg-db album))))) :_id)))

;; resources
(defresource random-image-in-album [name]
  :available-media-types ["application/json"]
  :exists? (fn [ctx] (some #{name} (get-albums)))
  :handle-ok (fn [ctx] (json/write-str (get-random-image name))))

(defresource albums-list
  :available-media-types ["application/json"]
  :handle-ok (fn [ctx] (json/write-str {:albums (get-albums)})))

(defresource random-image
  :available-media-types ["application/json"]
  :handle-ok (fn [ctx] (json/write-str (get-random-image))))

;; routes
(defroutes app-routes
  (GET "/random"              [] random-image)
  (GET "/random/albums"       [] albums-list)
  (GET "/random/album/:name"  [name] (random-image-in-album name))
  (route/not-found "Not Found"))

;; app
(def app
  (wrap-params (wrap-defaults app-routes site-defaults)))
