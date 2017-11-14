(ns narratology.foil.parse
  (:require [narratology.foil.grammar :as grammar]
            [clojure.tools.reader.edn :as edn]
            [clojure.tools.reader.reader-types :as reader-types]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [instaparse.core :as insta]))

(def string-parser
  (insta/parser (slurp "resources/string-grammar.bnf")))

(defn- maybe-wrap-join [params]
  (if (= 1 (count params))
    (first params)
    (concat (list 'join) params)))

(defn- parse-string [s]
  (s/conform
   ::grammar/expression
   (->> s
        string-parser
        rest
        (map (fn [[type contents]]
               (if (= :expression type)
                 (read-string contents)
                 contents)))
        maybe-wrap-join)))

(defn load-source [filename]
  (let [reader (reader-types/string-push-back-reader (slurp filename))
        opts   {:eof ::eof}]
    (->> #(edn/read opts reader)
         repeatedly
         (take-while #(not= % ::eof)))))

(defn parse-strings [tree]
  (walk/postwalk
   (fn [x]
     (if (and (vector? x) (= :string (first x)))
       (parse-string (second x))
       x))
   tree))

(defn parse-file [filename]
  (-> (s/conform ::grammar/grammar (load-source filename))
      parse-strings))
