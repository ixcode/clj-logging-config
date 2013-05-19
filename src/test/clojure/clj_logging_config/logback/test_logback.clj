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


(deftest test-default-logging
  (testing "Default logging" 
    (reset-logging!)
    (set-logger!)

    (expect "" (trace "Trace messages are hidden by default"))
    (expect "" (debug "Debug messages are hidden by default"))

    (expect "INFO - Here is a log message\n" (info  "Here is a log message"))
    (expect "WARN - Here is a warning\n"     (warn  "Here is a warning"))
    (expect "ERROR - Here is an error\n"     (error "Here is an error")))
)


(deftest test-logging-levels  
  (testing "Logging only happens at or above the current level"    
    (test-level :trace [:trace :debug :info :warn :error])
    (test-level :debug [:debug :info :warn :error])
    (test-level :info  [:info :warn :error])
    (test-level :warn  [:warn :error])
    (test-level :error [:error]))
)


;; (deftest test-ns-specific-logger
;;   (testing "Can set the logger for a namespace by name"
;;     (set-logger! "test.logback")
;;     (expect ""
;;             (info "This should not be output"))
;;     (within-ns "test.logback"
;;                (expect "INFO - This should be output")
;;                ))
;; )



