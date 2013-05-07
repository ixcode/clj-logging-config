;; clj-logging-config - Logging configuration for Clojure.

;; by Malcolm Sparks - logback contributed by Jim Barritt

;; Copyright (c) Malcolm Sparks. All rights reserved.

;; The use and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can
;; be found in the file epl-v10.html at the root of this distribution.  By using
;; this software in any fashion, you are agreeing to be bound by the terms of
;; this license.  You must not remove this notice, or any other, from this
;; software.

(ns clj-logging-config.logback
  (:use clojure.tools.logging)

  (:require [clojure.java.io :as io]
            [clojure.tools.logging.impl])

  (:import (ch.qos.logback.classic Level Logger LoggerContext)
           (ch.qos.logback.classic.encoder PatternLayoutEncoder)

           (ch.qos.logback.core ConsoleAppender)
           (ch.qos.logback.core.encoder Encoder)
           (ch.qos.logback.core.rolling 
            RollingFileAppender SizeAndTimeBasedFNATP TimeBasedRollingPolicy)
           (ch.qos.logback.core.util StatusPrinter)

           (net.logstash.logback.encoder LogstashEncoder)

           (org.slf4j LoggerFactory)

           (java.io OutputStream Writer File)))

(defn- get-logger-context []
  ^LoggerContext (LoggerFactory/getILoggerFactory))

(defn- get-root-logger []
  (LoggerFactory/getLogger Logger/ROOT_LOGGER_NAME))

(defn- create-pattern-encoder [pattern]
  (let [pattern-encoder (PatternLayoutEncoder.)]
    (doto pattern-encoder
      (.setPattern pattern)
      (.setContext (get-logger-context))
      (.start))
    pattern-encoder))

(defn- create-console-appender [context name pattern]
  (let [^ConsoleAppender console-appender (ConsoleAppender.)]
    (doto console-appender
      (.setContext context)
      (.setName name)
      (.setEncoder (create-pattern-encoder pattern))
      (.start))))

(defn reset-logging! []
  (.reset (get-logger-context)))

(defn set-logger! []
  (let [context (get-logger-context)
        root-logger (get-root-logger)]
    (doto root-logger
      (.setLevel Level/INFO)
      (.setAdditive true)
      (.addAppender (create-console-appender context "_default" "%level - %message%n")))

    (StatusPrinter/printInCaseOfErrorsOrWarnings context)))