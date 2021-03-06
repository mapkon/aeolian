(ns aeolian.core-test
  (:use midje.sweet)
  (:require [aeolian.core :as core]
            [me.raynes.fs :as fs]
            [clojure.string :as str]))

(defn- teardown [input-file-name]
  (fs/delete input-file-name)
  (fs/delete (str input-file-name ".abc")))

(facts "generating notation"
       (fact "fails if the supplied input file does not exist"
             (fs/exists? "foo.metrics") => falsey
             (core/generate-notation-from "foo.metrics" "foo.abc" {:duplicate-lines 10 :total-lines 100}) => (throws java.io.FileNotFoundException))

       (let [input-file-name (fs/temp-name "foo" ".metrics")
             output-file-name (core/notation-file-name input-file-name)]
         (against-background [(before :facts (spit input-file-name "{:source-file \"Foo.java\" :line 1 :indentation-violation? true :line-length \"30\"}"))
                              (after :facts (teardown input-file-name))]
                             (fact "succeeds if the supplied input file exists"
                                   (fs/exists? output-file-name) => falsey
                                   (fs/exists? input-file-name) => truthy
                                   (core/generate-notation-from input-file-name output-file-name {:duplicate-lines 10 :total-lines 100})
                                   (fs/exists? output-file-name) => truthy))))

(fact "generating notation file name is based on original-file-name"
      (core/notation-file-name "foo") => "foo.abc")
