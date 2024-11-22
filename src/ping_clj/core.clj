(ns ping-clj.core
  (:import
   (com.zaxxer.ping IcmpPinger PingTarget)
   (java.net InetAddress)))

;; NOTE: if the follow exception occurs:
;; Unable to create IPv4 socket.  If this is Linux, you might need to
;; set sysctl net.ipv4.ping_group_range
;; i.g. sudo sysctl net.ipv4.ping_group_range='10001 10001'

(defn response-handler [& {:keys [on-response on-timeout]}]
  (reify com.zaxxer.ping.PingResponseHandler
    (onResponse [this target time-sec byte-count ping-seq]
      (let [host (-> target
                     .getInetAddress
                     .getHostAddress)]
        (if (nil? on-response)
          (println (str byte-count " bytes"
                        " from " host
                        " icmp_seq=" ping-seq
                        " time=" time-sec
                        "\n"))
          (on-response {:time      time-sec
                        :host      host
                        :icmp-seq  ping-seq
                        :num-bytes byte-count}))))
    (onTimeout [this target]
      (if (nil? on-timeout)
        (println "timeout: " target)
        (on-timeout target)))))

(defn ping [host & {:keys [on-response on-timeout]}]
  (let [inet-obj (InetAddress/getByName host)
        pinger   (new IcmpPinger (response-handler
                                  :on-response on-response
                                  :on-timeout on-timeout))
        target   (new PingTarget inet-obj)]
    (future (try (.runSelector pinger)
                 (catch Exception e (println e))))
    (.ping pinger target)))
