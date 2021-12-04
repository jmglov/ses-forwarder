(ns ses-forwarder.core
  (:require [cognitect.aws.client.api :as aws])
  (:import (java.io ByteArrayInputStream)
           (java.util Properties)
           (jakarta.mail Session Transport)
           (jakarta.mail.internet MimeMessage)))

(defn create-aws-client [service]
  (aws/client {:api service}))

(defn get-secret [ssm-client name]
  (->> (aws/invoke ssm-client
                   {:op :GetParameter
                    :request {:Name name
                              :WithDecryption true}})
       :Parameter
       :Value))

(defn get-object [s3-client s3-bucket s3-key]
  (->> (aws/invoke s3-client
                   {:op :GetObject
                    :request {:Bucket s3-bucket
                              :Key s3-key}})
       :Body
       slurp))

(defn create-smtp-session [host]
  (Session/getInstance (doto (Properties.)
                         (.setProperty "mail.smtp.host" host)
                         (.setProperty "mail.smtp.ssl.enable" "true"))))

(defn create-message [session msg-str]
  (->> msg-str
       .getBytes
       (ByteArrayInputStream.)
       (MimeMessage. session)))

(defn send-message [username password msg]
  (Transport/send msg username password))
