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
        clj-logging-config.logback)
  (:require [clojure.java.io :as io]))

;; Copied from clojure.contrib.with-ns
(defmacro with-ns
  "Evaluates body in another namespace.  ns is either a namespace
  object or a symbol.  This makes it possible to define functions in
  namespaces other than the current one."
  [ns & body]
  `(binding [*ns* (the-ns ~ns)]
     ~@(map (fn [form] `(eval '~form)) body)))

;; Copied from clojure.contrib.with-ns
(defmacro with-temp-ns
  "Evaluates body in an anonymous namespace, which is then immediately
  removed.  The temporary namespace will 'refer' clojure.core."
  [& body]
  `(do (create-ns 'sym#)
       (let [result# (with-ns 'sym#
                       (clojure.core/refer-clojure)
                       ~@body)]
         (remove-ns 'sym#)
         result#)))

(defmacro capture-stdout [& body]
  `(let [out# System/out
         baos# (java.io.ByteArrayOutputStream.)
         tempout# (java.io.PrintStream. baos#)]
     (try
       (System/setOut tempout#)
       ~@body
       (String. (.toByteArray baos#))
       (finally
         (System/setOut out#)))))


(defmacro expect [expected & body]
  `(is (= ~expected (capture-stdout (do ~@body)))))


(deftest test-default-logging

  (testing "Default logging" 
    (reset-logging!)
    (set-logger!)

    (expect ""            
            (trace "Debug messages are hidden by default"))

    (expect ""            
            (debug "Debug messages are hidden by default"))

    (expect "INFO - Here is a log message\n"            
            (info "Here is a log message"))

    (expect "WARN - Here is a warning\n"              
            (warn "Here is a warning"))

    (expect "ERROR - Here is an error\n"              
            (error "Here is an error"))

))

(defn expected-message [level levels-with-output message]    
  (if (some #{level} levels-with-output)
    (format "%s - %s\n" (clojure.string/upper-case (name level)) message)
    ""))

(defn expect-levels [level-to-set levels-with-output]
 
  (let [message "message"]
    (reset-logging!)
    (set-logger! :level level-to-set)
    (expect (expected-message :trace levels-with-output message)             
             (trace message))
    (expect (expected-message :debug levels-with-output message)
             (debug message))
    (expect (expected-message :info levels-with-output message)
             (info message))
    (expect (expected-message :warn levels-with-output message)
             (warn message))
    (expect (expected-message :error levels-with-output message)
             (error message))
))

(deftest test-logging-levels
  
  (testing "Logging only happens at or above the current level"    
    (expect-levels :trace [:trace :debug :info :warn :error])
    (expect-levels :debug [:debug :info :warn :error])
    (expect-levels :info  [:info :warn :error])
    (expect-levels :warn  [:warn :error])
    (expect-levels :error [:error]))
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



