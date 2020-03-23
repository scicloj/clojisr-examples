(ns clojisr-examples.template
  (:require [notespace.v2.note :as note
             :refer [note note-void note-md check]]
            [notespace.v2.live-reload]
            [clojisr.v1.r :as r]))

(note-md "Here is a basic example of using clojisr for R-interop. Use this example as a template for other examples.")

(note-void
 (require '[clojisr.v1.r :as r :refer [r]]
          '[clojisr.v1.require :refer [require-r]])
 (r/discard-all-sessions)
 (require-r '[stats]))

(note
 (r '(+ 1 [1 2 3])))

(note
 (->> [1 2 4]
      r.stats/median
      r/r->clj
      (check = [2.0])))

(comment (notespace.v2.note/compute-this-notespace!))
