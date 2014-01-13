(require '[clojure.java.io :as io])
(use 'clojure.tools.logging 'clj-logging-config.logback)



;; First clear out any of the appenders that are currently configured
(reset-logging!)

;; Now set up the default logger:
(set-logger!)
(info "Just a plain logging message, you should see the level at the beginning")
