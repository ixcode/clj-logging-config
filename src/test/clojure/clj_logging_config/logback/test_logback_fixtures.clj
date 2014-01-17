(ns clj-logging-config.logback.test-logback-fixtures
  (:use clojure.test
        clojure.tools.logging
        clj-logging-config.logback
        clj-logging-config.logback.logback-fixtures)
  (:require [clojure.java.io :as io]))


(deftest test-capture-std-out
  (testing "That we can capture the standard output"
    (is (= "Hello World\n" (capture-stdout (. System/out println "Hello World"))))))

(deftest test-expect
  (testing "That we can successfully capture std out"
    (expect "Hello World\n" (println "Hello World"))))
