(ns aeolian.composer-test
  (:use midje.sweet)
  (:require [aeolian.composer :as c]
            [clojure.string :as str]
            [aeolian.tempo :as t]
            [aeolian.abc.notes :as n]))

(facts "when processing metrics"
  (facts "line length is mapped to note"
    (future-fact "empty lines are mapped to rests"
      (c/metric-to-note "Foo.java#1 LL=0") => n/rest-note)

    (facts "short lines are mapped to rests"
      (c/metric-to-note "Foo.java#1 LL=1") => n/rest-note
      (c/metric-to-note "Foo.java#1 LL=2") => n/rest-note
      (c/metric-to-note "Foo.java#1 LL=3") => n/rest-note
      (c/metric-to-note "Foo.java#1 LL=4") => n/rest-note
      (c/metric-to-note "Foo.java#1 LL=5") => n/rest-note
      (c/metric-to-note "Foo.java#1 LL=6") => n/rest-note
      (c/metric-to-note "Foo.java#1 LL=7") => n/rest-note
      (c/metric-to-note "Foo.java#1 LL=8") => n/rest-note
      (c/metric-to-note "Foo.java#1 LL=9") => n/rest-note
      )

    (facts "longer lines are mapped to actual notes with longer lines at higher octaves"
      (some #(= (c/metric-to-note "Foo.java#1 LL=10") %) n/octave-1 ) => truthy
      (some #(= (c/metric-to-note "Foo.java#1 LL=39") %) n/octave-1 ) => truthy
      (some #(= (c/metric-to-note "Foo.java#1 LL=40") %) n/octave-2 ) => truthy
      (some #(= (c/metric-to-note "Foo.java#1 LL=79") %) n/octave-2 ) => truthy
      (some #(= (c/metric-to-note "Foo.java#1 LL=80") %) n/octave-3 ) => truthy
      (some #(= (c/metric-to-note "Foo.java#1 LL=99") %) n/octave-3 ) => truthy
      (some #(= (c/metric-to-note "Foo.java#1 LL=100") %) n/octave-4 ) => truthy
      (some #(= (c/metric-to-note "Foo.java#1 LL=119") %) n/octave-4 ) => truthy
      (some #(= (c/metric-to-note "Foo.java#1 LL=120") %) n/octave-5 ) => truthy
      (some #(= (c/metric-to-note "Foo.java#1 LL=121") %) n/octave-5 ) => truthy
      (some #(= (c/metric-to-note "Foo.java#1 LL=200") %) n/octave-5 ) => truthy
      (some #(= (c/metric-to-note "Foo.java#1 LL=2000") %) n/octave-5 ) => truthy
       )

    )

  (fact "complexity > 1 is mapped to tempo"
    (str/index-of (c/metric-to-note "Foo.java#1 LL=30 CC=1") t/prefix ) => falsey
    (str/index-of (c/metric-to-note "Foo.java#1 LL=30 CC=10") t/prefix ) => truthy
    (str/index-of (c/metric-to-note "Foo.java#1 LL=30 CC=5") t/prefix ) => truthy
    (str/index-of (c/metric-to-note "Foo.java#1 LL=30 CC=3") t/prefix ) => truthy)

  (future-fact "method-length > 5 is mapped to tempo"
    (str/index-of (c/metric-to-note "Foo.java#1 LL=30 ML=1") t/prefix ) => falsey
    (str/index-of (c/metric-to-note "Foo.java#1 LL=30 ML=10") t/prefix ) => truthy
    (str/index-of (c/metric-to-note "Foo.java#1 LL=30 ML=5") t/prefix ) => truthy
    (str/index-of (c/metric-to-note "Foo.java#1 LL=30 ML=3") t/prefix ) => truthy)
  )

(facts "when opening metrics files"
  (fact "all lines are used in composition"
    (c/compose ["/home/amarks/Code/aeolian/resources/Notification.java#1 LL=3"
                "/home/amarks/Code/aeolian/resources/Notification.java#10 LL=70"
                "/home/amarks/Code/aeolian/resources/Notification.java#100 LL=99"
                        ]) => truthy ))