(ns clojisr-examples.graph-gallery.one-num
  (:require [notespace.v2.note :as note
             :refer [note note-void note-md note-as-md note-hiccup note-as-hiccup]]
            [clojisr.v1.r :as r]))

(def target-path (notespace.v2.note/ns->out-dir *ns*))

(note-md "# [from Data to Viz](www.data-to-viz.com) - [AIRBNB PRICES ON THE FRENCH RIVIERA](https://www.data-to-viz.com/story/OneNum.html)")
(note-md "Code from [project](www.data-to-viz.com) by Yan Holtz and Conor Healy.")
(note-md "You can find here only translated code, please refer [original text](https://www.data-to-viz.com/story/OneNum.html) for explanation.")

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
                      '[ggExtra :as gge]
                      '[viridis :as viridis]
                      '[forcats :as forcats]
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

(note-void (def data (u/read-table "https://raw.githubusercontent.com/holtzy/data_to_viz/master/Example_dataset/1_OneNum.csv"
                                   :header true)))

(note-as-hiccup (-> data
                    (u/head 6)
                    (knitr/kable)
                    (r->clj)
                    (first)))

(note-md "## Histogram")

(note-void (defn histogram [step]
             (r+ (gg/ggplot (dplyr/filter data '(< price 300)))
                 (gg/aes :x 'price)
                 (gg/stat_bin :breaks (base/seq 0 300 step)
                              :fill "#69b3a2"
                              :color "#e9ecef"
                              :alpha 0.9)
                 (gg/ggtitle "Night price distribution of Airbnb appartements")
                 (th/theme_ipsum_rc)                                                     
                 (gg/theme :plot.title (gg/element_text :size 12)))))

(note-void (plot->file (str target-path "a.png") (histogram 10)))
(note-void (plot->file (str target-path "b.png") (histogram 3)))
(note-hiccup [:div
              [:image {:src "a.png"}]
              [:image {:src "b.png"}]])

(note-md "## Density")

(note-void (defn density [bandwidth]
             (r+ (gg/ggplot (dplyr/filter data '(< price 300)))
                 (gg/aes :x 'price)
                 (gg/geom_density :fill "#69b3a2"
                                  :color "#e9ecef"
                                  :alpha 0.7
                                  :bw bandwidth)
                 (gg/ggtitle (str "Bandwidth: " bandwidth))
                 (th/theme_ipsum_rc))))

(note-void (plot->file (str target-path "c.png") (density 10)))
(note-void (plot->file (str target-path "d.png") (density 2)))
(note-hiccup [:div
              [:image {:src "c.png"}]
              [:image {:src "d.png"}]])


(comment (notespace.v2.note/compute-this-notespace!))
(comment (r/discard-all-sessions))

(comment (r "x11()"))
(comment (r "dev.off()"))
