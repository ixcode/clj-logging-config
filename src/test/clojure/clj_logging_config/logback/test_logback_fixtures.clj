(ns clj-logging-config.logback.test-logback-fixtures
  (:use clojure.test
        clojure.tools.logging
        clj-logging-config.logback
        clj-logging-config.logback.logback-fixtures)
  (:require [clojure.java.io :as io]))


(deftest test-capture-std-out
  (testing "capture the standard output"
    (is (= "Hello World\n" (capture-stdout (. System/out println "Hello World"))))))

(deftest test-expect
  (testing "capture std out and match to an expected string"
    (expect "Hello World\n" (. System/out println "Hello World"))))

(defn test-fn [a b]
  (format "My params are [%s] and [%s]" a b))

(deftest test-execute-keyword-fn
  (testing "invoke a function named the same as a keyword"
    (is (= "My params are [foo] and [bar]" (execute-keyword-fn :test-fn "foo" "bar")))))

(deftest test-expected-message
  (testing "check that can assert against a log level"
    (reset-logging!)
    (set-logger!)
    (is (= "INFO - Hello World\n" (expected-message :info [:info] "Hello World")))))


(deftest test-check-output-levels
  (testing "checks output at each level"
    (reset-logging!)
    (set-logger! :level :info :pattern "%level - %message%n")
    (check-output-levels [:info :warn :error] "Hello World")))
