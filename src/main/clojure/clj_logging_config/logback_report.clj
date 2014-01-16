(ns clj-logging-config.logback-report
  "Contains functions to report on how logback is currently configured"
  (:use clj-logging-config.logback-api)
  (:import (ch.qos.logback.classic Level Logger LoggerContext)
           (ch.qos.logback.classic.encoder PatternLayoutEncoder)

           (ch.qos.logback.core Appender ConsoleAppender)
           (ch.qos.logback.core.encoder Encoder)
           (ch.qos.logback.core.rolling RollingFileAppender 
                                        SizeAndTimeBasedFNATP 
                                        TimeBasedRollingPolicy)
           (ch.qos.logback.core.util StatusPrinter)

           (net.logstash.logback.encoder LogstashEncoder)

           (org.slf4j LoggerFactory)

           (java.io OutputStream Writer File)
           (java.util Iterator)))
 
 (defmacro transform-seq [transformFn seq]
  `(apply vector (map ~transformFn ~seq)))


(defmacro exclude-keys [map & excludedPropertyKeys]
  "Returns a map excluding the named property keys"
  `(dissoc ~map ~@excludedPropertyKeys))

(defn seq-from-iterator [^Iterator iterator]
  (loop [itr iterator
         result []] 
     (if (. itr hasNext)
       (recur itr (cons (. itr next) result))
       result)))

(defn appender-as-map [^Appender appender]
  appender)

(defn logger-as-map [^Logger logger]
  (let [allProperties (bean logger)
        coreProperties (exclude-keys allProperties 
                                     :loggerContext :traceEnabled :warnEnabled :infoEnabled :debugEnabled :errorEnabled)
        appenderItr (. logger iteratorForAppenders)]    
    (assoc coreProperties
      :appenders (transform-seq appender-as-map (seq-from-iterator appenderItr)))))


(defn logback-configuration-as-map [^LoggerContext loggerContext]  
  (let [allProperties (bean loggerContext)
        coreProperties (exclude-keys allProperties
                                    :configurationLock :turboFilterList
                                    :loggerContextRemoteView :statusManager :frameworkPackages 
                                    :executorService :copyOfListenerList :copyOfPropertyMap)]
    (assoc coreProperties
      :loggers (transform-seq logger-as-map (:loggerList allProperties)))))


(defn println-logback-configuration []
  (println (pprint (logback-configuration-as-map (get-logback-context)))))
