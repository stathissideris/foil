(ns narratology.foil.grammar
  (:require [clojure.spec.alpha :as s]))

(s/def ::operator #{'eval 'one-of 'join 'split 'plural})

(s/def ::call
  (s/cat :op ::operator
         :params (s/* ::expression)))

(s/def ::expression
  (s/or :let ::let
        :call ::call
        :ref symbol?
        :string string?))

(s/def ::binding
  (s/cat :name symbol?
         :exp ::expression))

(s/def ::let
  (s/cat :let #{'let}
         :bindings (s/spec (s/+ ::binding))
         :body ::expression))

(s/def ::node-body ::expression)

(s/def ::node
  (s/cat :type #{'node 'origin}
         :name symbol?
         :body ::node-body))

(s/def ::grammar
  (s/or :nodes (s/coll-of ::node)
        :let ::let))
