(ns aeolian.core
	(:require [aeolian.parser :as parser]
						[aeolian.tempo :as tempo]
						[aeolian.composer :as composer]
						[clojure.java.io :as io]))

(defn notation-file-name [original-file-name]
	(str original-file-name ".abc"))

(defn- generate-notation-from [metrics-file-name]
	; (println (str "Generating ABC Notation from " metrics-file-name "..."))
	(with-open [rdr (clojure.java.io/reader metrics-file-name)]
     	(let [notation-file-name (notation-file-name metrics-file-name)
     				composition (composer/compose (line-seq rdr))]
 				(spit notation-file-name, composition)
     		(println (str "Generated " notation-file-name))))
	)

(defn -main [& args]
	(if-let [metrics-file-name (first args)]
		(if (.exists (io/as-file metrics-file-name))
			(generate-notation-from metrics-file-name)
			(println (str "Error: cannot find metrics file - " metrics-file-name)))
		(println "Error: no metrics file supplied")))
