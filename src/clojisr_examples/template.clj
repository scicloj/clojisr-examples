(ns clojisr-examples.template
  (:require [notespace.v1.note :as note
             :refer [note note-void note-md]]))

(note-md "Here is a basic example of using clojisr for R-interop. Use this example as a template for other examples.")

(note-void
 (require '[clojisr.v1.r :as r :refer [r]]
          '[clojisr.v1.require :refer [require-r]])
 (require-r '[stats]))

(note
 (r '[+ 1 [1 2 3]]))

(note
 (r.stats/median [1 2 4]))

(note/render-this-ns!)
