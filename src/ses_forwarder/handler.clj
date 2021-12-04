(ns ses-forwarder.handler
  (:gen-class
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler])
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]))

(defn forward-email [email]
  (println "Received message ID" (:messageId email)
           "from" (:source email)
           "to" (:destination email))
  email)

(defn forward-emails [event]
  (->> event
       :Records
       (map (comp forward-email
                  #(select-keys % [:messageId :source :destination])
                  #(get-in % [:ses :mail])))))

(defn -handleRequest [this is os context]
  (let [w (io/writer os)]
    (-> (json/read (io/reader is) :key-fn keyword)
        forward-emails
        (json/write w))
    (.flush w)))
