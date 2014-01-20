(ns clj-logging-config.logback-report
  "Contains functions to report on how logback is currently configured"
  (:use clj-logging-config.logback-api clojure.pprint)
  (:import (ch.qos.logback.classic Level Logger LoggerContext)
           (ch.qos.logback.classic.encoder PatternLayoutEncoder)

           (ch.qos.logback.core Appender ConsoleAppender Layout)
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

(defn layout-as-map [^Layout layout]
  (let [allProperies (bean layout)
        coreProperties (exclude-keys allProperies :effectiveConverterMap :defaultConverterMap :context :statusManager :instanceConverterMap)]
    coreProperties))

(defn encoder-as-map [^Encoder encoder]
  (let [allProperies (bean encoder)
        coreProperties (exclude-keys allProperies :context :statusManager :layout)]
    (assoc coreProperties
      :layout (layout-as-map (:layout allProperies)))))

(defn appender-as-map [^Appender appender]
  (let [allProperties (bean appender)
        coreProperties (exclude-keys allProperties 
                                     :context :copyOfAttachedFiltersList :statusManager 
                                     :encoder :outputStream)]
    (assoc coreProperties
      :encoder (encoder-as-map (:encoder allProperties)))))

(defn logger-as-map [^Logger logger]
  (let [allProperties (bean logger)
        coreProperties (exclude-keys allProperties 
                                     :loggerContext 
                                     :traceEnabled :warnEnabled :infoEnabled :debugEnabled :errorEnabled
                                     :level :effectiveLevel)
        appenderItr (. logger iteratorForAppenders)]    
    (assoc coreProperties
      :appenders (transform-seq appender-as-map (seq-from-iterator appenderItr))
      :level (str (:level allProperties))
      :effectiveLevel (str (:effectiveLevel allProperties)))))


(defn logback-configuration-as-map [^LoggerContext loggerContext]  
  (let [allProperties (bean loggerContext)
        coreProperties (exclude-keys allProperties
                                    :configurationLock :loggerList :turboFilterList
                                    :loggerContextRemoteView :statusManager :frameworkPackages 
                                    :executorService :copyOfListenerList :copyOfPropertyMap)]
    (assoc coreProperties
      :loggers (transform-seq logger-as-map (:loggerList allProperties)))))


(defn println-logback-configuration []
  (println (pprint (logback-configuration-as-map (get-logback-context)))))
