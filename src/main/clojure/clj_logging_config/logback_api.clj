(ns clj-logging-config.logback-api
  "Wrappers for important logback classes and functions"
  (:import (ch.qos.logback.classic Level Logger LoggerContext)
           (ch.qos.logback.classic.encoder PatternLayoutEncoder)

           (ch.qos.logback.core ConsoleAppender)
           (ch.qos.logback.core.encoder Encoder)
           (ch.qos.logback.core.rolling RollingFileAppender 
                                        SizeAndTimeBasedFNATP 
                                        TimeBasedRollingPolicy)
           (ch.qos.logback.core.util StatusPrinter)

           (net.logstash.logback.encoder LogstashEncoder)

           (org.slf4j LoggerFactory)

           (java.io OutputStream Writer File)))

(defn get-logback-context []
  (LoggerFactory/getILoggerFactory))

