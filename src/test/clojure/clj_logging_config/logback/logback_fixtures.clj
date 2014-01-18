(ns clj-logging-config.logback.logback-fixtures
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
  `(is (= ~expected (capture-stdout ~@body))))

(defmacro expect-re-find [expected & body]
  `(is (re-find ~expected (capture-stdout ~@body))))

(defmacro expect-empty [& body]
  `(is (empty? (capture-stdout ~@body))))


(defmacro execute-keyword-fn [keyword & args]
  `(~(symbol (name keyword)) ~@args)) 

(defmacro expected-message [level levels-with-output message]    
  `(if (some #{~level} ~levels-with-output)
    (format "%s - %s\n" (clojure.string/upper-case (name ~level)) ~message)
    ""))

(defmacro confirm-level-output [level expected-outputs message]
  `(is (= (expected-message ~level ~expected-outputs ~message) 
         (capture-stdout (execute-keyword-fn ~level ~message)))))

(defn check-output-levels [expected-levels  message]
  (confirm-level-output :trace expected-levels message)
  (confirm-level-output :debug expected-levels message)
  (confirm-level-output :info  expected-levels message)
  (confirm-level-output :warn  expected-levels message)
  (confirm-level-output :error expected-levels message))


(defn test-level
  "For each `level-to-set', set it and then see if the corresponding `levels-with-output' show correctly " 
  [level-to-set levels-with-output]
  (let [message "message"]
    (reset-logging!)
    (set-logger! :level level-to-set :pattern "%level - %message%n")
    (check-output-levels levels-with-output message)))

