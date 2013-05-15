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

(defmacro dolog [& body]
  `(do (reset-logging!)
       (let [ns# (create-ns (symbol "test.logback"))]
         (with-ns ns#
           (clojure.core/refer-clojure)
           (use 'clojure.tools.logging 'clj-logging-config.logback)
           ~@body))))

(defmacro expect [expected & body]
  `(is (= ~expected (capture-stdout (dolog ~@body)))))


(deftest test-default-logging

  (testing "Default logging" 

    (expect "INFO - Here is a log message\n"            
            (set-logger!)
            (info "Here is a log message"))

    (expect "WARN - Here is a warning\n"              
            (set-logger!)
            (warn "Here is a warning"))

    (expect ""            
            (set-logger!)
            (debug "Debug messages are hidden by default")))

)

(deftest test-logging-levels
  (testing "Logging at the DEBUG level"

    (expect "DEBUG - Debug level messages are now shown\n"
            (set-logger! :level :debug)
            (debug "Debug level messages are now shown")))
)






