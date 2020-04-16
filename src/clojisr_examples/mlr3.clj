(ns clojisr-examples.mlr3
  (:require [notespace.v2.note :as note
             :refer [note note-void note-md note-as-md note-hiccup note-as-hiccup]]
            [clojisr.v1.r :as r]))

(note/defkind note-def :def {:render-src?    true
                             :value-renderer (comp notespace.v2.view/value->hiccup var-get)})

(note-md "# MLR3 package usage example.")

(note-void (require '[clojisr.v1.r :as r :refer [r+ r* r r->clj clj->r clj->java java->clj java->r r->java]]
                    '[clojisr.v1.require :refer [require-r]]
                    '[clojisr.v1.applications.plotting :refer [plot->file]]))

(note-void (require-r '[base :as base :refer [$]]
                      '[mlr3 :as mlr3]))

(note-md "## create task and learner object")

(note-def (def task-iris (($ mlr3/TaskClassif 'new) :id "iris" :backend 'iris :target "Species")))
(note-def (def learner (mlr3/lrn "classif.rpart" :cp 0.01)))

(note-md "## train and test sets")

(note-def (def train-set (base/sample ($ task-iris 'nrow) (r* 0.8 ($ task-iris 'nrow)))))
(note-def (def test-set (base/setdiff (base/seq_len ($ task-iris 'nrow)) train-set)))

(note-md "## train")

(note (($ learner 'train) task-iris :row_ids train-set))

(note-md "## predict")

(note-def (def prediction (($ learner 'predict) task-iris :row_ids test-set)))

(note ($ prediction 'confusion))
(note (r->clj ($ prediction 'confusion)))
;; => {["setosa" "setosa"] 10,
;;     ["setosa" "versicolor"] 0,
;;     ["setosa" "virginica"] 0,
;;     ["versicolor" "setosa"] 0,
;;     ["versicolor" "versicolor"] 10,
;;     ["versicolor" "virginica"] 1,
;;     ["virginica" "setosa"] 0,
;;     ["virginica" "versicolor"] 1,
;;     ["virginica" "virginica"] 8}

(note-md "## check accuracy")

(note-def (def measure (mlr3/msr "classif.acc")))

(note (($ prediction 'score) measure))
;; => classif.acc 
;; 0.9333333 

(note-md "## cross-validation")

(note-def (def resampling (mlr3/rsmp "cv" :folds 3)))
(note-def (def rr (mlr3/resample task-iris learner resampling)))

(note (($ rr 'aggregate) measure))
;; => classif.acc 
;; 0.9266667

(comment (notespace.v2.note/compute-this-notespace!))
(comment (r/discard-all-sessions))
