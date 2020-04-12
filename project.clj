(defproject clojisr-examples "1.0.0-BETA11"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [scicloj/clojisr "1.0.0-BETA11-SNAPSHOT"]
                 [scicloj/notespace "2.0.0-alpha4"]]
  :jvm-opts ["-Dclojure.tools.logging.factory=clojure.tools.logging.impl/jul-factory"])
