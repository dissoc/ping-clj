(ns ping-clj.core-test
  (:require [clojure.test :refer :all]
            [ping-clj.core :refer :all]))

(deftest ping-test
  (testing "testing ping"
    (let [result (promise)]
      (ping "127.0.0.1"
            :on-response #(deliver result %))
      (let [{time      :time
             host      :host
             icmp-seq  :icmp-seq
             num-bytes :num-bytes} @result]
        (is (and (number? time)
                 (= host "127.0.0.1")
                 (number? icmp-seq)
                 (number? num-bytes)))))))
