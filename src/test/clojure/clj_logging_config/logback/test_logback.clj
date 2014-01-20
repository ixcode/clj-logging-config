;; Copyright (c) Malcolm Sparks. All rights reserved.

;; The use and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can
;; be found in the file epl-v10.html at the root of this distribution.  By using
;; this software in any fashion, you are agreeing to be bound by the terms of
;; this license.  You must not remove this notice, or any other, from this
;; software.

(ns clj-logging-config.logback.test-logback
  (:use clojure.test
        clojure.tools.logging
        clj-logging-config.logback
        clj-logging-config.logback.logback-fixtures)
  (:require [clojure.java.io :as io]))

(deftest simplest-test
  (testing "Just the basics"
    (reset-logging!)
    (set-logger!)
    (expect-re-find #"Hello World$" (info "Hello World"))))

(deftest test-default-logging
  (testing "Default logging" 
    (reset-logging!)
    (set-logger!)

    (expect-empty (trace "Trace messages are hidden by default"))
    (expect-empty (debug "Debug messages are hidden by default"))

    (expect-re-find #"Here is a log message$" (info  "Here is a log message"))
    (expect-re-find #"Here is a warning$"     (warn  "Here is a warning"))
    (expect-re-find #"Here is an error\n"     (error "Here is an error")))
)


(deftest test-logging-levels  
  (testing "Logging only happens at or above the current level"    
    (test-level :trace [:trace :debug :info :warn :error])
    (test-level :debug [:debug :info :warn :error])
    (test-level :info  [:info :warn :error])
    (test-level :warn  [:warn :error])
    (test-level :error [:error])))


(deftest test-set-logger-with-pattern
  (testing "can set a pattern on the logger"
    (reset-logging!)
    (set-logger! :pattern "%level --foo-- %message%n")
    (expect "INFO --foo-- hello johnny\n" (info "hello johnny"))))

(deftest test-set-logger-with-appender
  (testing "can set a pattern on the logger"
    (reset-logging!)
    (set-logger! :pattern "%level --foo-- %message%n")
    (expect "INFO --foo-- hello johnny\n" (info "hello johnny"))))


;; (deftest test-ns-specific-logger
;;   (testing "Can set the logger for a namespace by name"
;;     (reset-logging!)
;;     (set-logger! "test.logback")

;;     (expect "" (info "This should not be output"))

;;     (within-ns "test.logback"
;;                (expect "INFO - This should be output" (info "This should be output")))))



