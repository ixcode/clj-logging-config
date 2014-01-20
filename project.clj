(defproject ureporter-simulator "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.logging "0.2.3"]
                 [log4j "1.2.16"]
                 [ch.qos.logback/logback-core "1.0.12"]
                 [ch.qos.logback/logback-classic "1.0.12"]
                 [net.logstash.logback/logstash-logback-encoder "1.2"]
                 [org.codehaus.jackson/jackson-mapper-asl "1.9.12"]
                 ]

  :source-paths ["src" "src/main/clojure"]
  :test-paths   ["test" "src/test/clojure"]
)
