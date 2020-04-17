(ns clojisr-examples.graph-gallery.violin
  (:require [notespace.v2.note :as note
             :refer [note note-void note-md note-as-md note-hiccup note-as-hiccup]]
            [clojisr.v1.r :as r]))

(def target-path (notespace.v2.note/ns->out-dir *ns*))

(note-md "# [R Graph Gallery](https://www.r-graph-gallery.com/) - [Violin](https://www.r-graph-gallery.com/violin.html)")
(note-md "Code from [project](https://www.r-graph-gallery.com/) by Yan Holtz")
(note-md "You can find here only translated code, please refer [original text](https://www.r-graph-gallery.com/violin.html)")

(note-md "## Setup")

(note-void (require '[clojisr.v1.r :as r :refer [r+ r* r r->clj clj->r bra colon]]
                    '[clojisr.v1.require :refer [require-r]]
                    '[clojisr.v1.applications.plotting :refer [plot->file]]))

(note-void (require-r '[base :as base :refer [$ <-]]
                      '[utils :as u]
                      '[stats :as stats]
                      '[graphics :as g]
                      '[grDevices :as dev]
                      '[tidyverse]
                      '[knitr :as knitr]
                      '[dplyr :as dplyr]
                      '[tidyr :as tidyr]
                      '[ggplot2 :as gg]
                      ;; '[ggExtra :as gge]
                      '[viridis :as viridis]
                      '[forcats]
                      '[extrafont]
                      '[hrbrthemes :as th]
                      '[datasets :refer :all]))

(note-md "WARNING: To use `hrbrthemes` you may want to:

1. Install Arial Narrow or Roboto Condensed fonts.
2. Register system fonts with `extrafont::font_import()` or `(r.extrafont/font_import)`
3. Fix font database as described in [here](https://github.com/hrbrmstr/hrbrthemes/issues/18#issuecomment-299692978)
4. Call `hrbrthemes::import_roboto_condensed()` or `(th/import_roboto_condensed)` 
5. Restart session")

(note-void (r.extrafont/loadfonts :quiet true))
(note-void (base/options :knitr.table.format "html"))
(note-void (base/set-seed 7337))

(note-md "## GGPlot2")

(note-md "### [Most basic violin chart](https://www.r-graph-gallery.com/95-violin-plot-with-ggplot2.html)")

(note-void (plot->file (str target-path "a.png")
                       (let [data (base/data-frame :name [(repeat 500 "A")
                                                          (repeat 500 "B")
                                                          (repeat 500 "B")
                                                          (repeat 20 "C")
                                                          (repeat 100 "D")]
                                                   :value [(stats/rnorm 500 10 5)
                                                           (stats/rnorm 500 13 1)
                                                           (stats/rnorm 500 18 1)
                                                           (stats/rnorm 20 25 4)
                                                           (stats/rnorm 100 12 1)])]
                         (r+ (gg/ggplot data (gg/aes :x 'name :y 'value :fill 'name))
                             (gg/geom_violin)))))
(note-hiccup [:image {:src "a.png"}])

(note-void (def data-wide (bra iris nil (colon 1 4))))

(note-as-md (-> data-wide
                (u/head)
                (knitr/kable)
                (r->clj)
                (first)))

(note-void (plot->file (str target-path "b.png")
                       (let [data (-> data-wide
                                      (tidyr/gather :key "MesureType" :value "Val"))]
                         (r+ (gg/ggplot data (gg/aes :x 'MesureType :y 'Val :fill 'MesureType))
                             (gg/geom_violin)))))
(note-hiccup [:image {:src "b.png"}])

(note-md "### [Control group order](https://www.r-graph-gallery.com/267-reorder-a-variable-in-ggplot2.html)")

(note-void (def mpg (base/$<- gg/mpg 'class (base/with gg/mpg '(reorder class hwy median)))))

(note-void (plot->file (str target-path "c.png")
                       (r+ (gg/ggplot mpg (gg/aes :x 'class :y 'hwy :fill 'class))
                           (gg/geom_violin)
                           (gg/xlab "")
                           (gg/theme :legend.position "none"))))
(note-hiccup [:image {:src "c.png"}])


(comment (notespace.v2.note/compute-this-notespace!))
(comment (r/discard-all-sessions))

(comment (r "x11()"))
(comment (r "dev.off()"))
