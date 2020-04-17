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

(note-void (def data-random (base/data-frame :name [(repeat 500 "A")
                                                    (repeat 500 "B")
                                                    (repeat 500 "B")
                                                    (repeat 20 "C")
                                                    (repeat 100 "D")]
                                             :value [(stats/rnorm 500 10 5)
                                                     (stats/rnorm 500 13 1)
                                                     (stats/rnorm 500 18 1)
                                                     (stats/rnorm 20 25 4)
                                                     (stats/rnorm 100 12 1)])))

(note-void (plot->file (str target-path "a.png")
                       (r+ (gg/ggplot data-random (gg/aes :x 'name :y 'value :fill 'name))
                           (gg/geom_violin))))
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

(note-md "### [Horizontal version](https://www.r-graph-gallery.com/violin_horizontal_ggplot2.html)")

(note-void (def data (-> "https://raw.githubusercontent.com/zonination/perceptions/master/probly.csv"
                         (u/read-table :header true :sep ",")
                         (tidyr/gather :key "text" :value "value")
                         (dplyr/mutate :text '(gsub "\\\\." " " text))
                         (dplyr/mutate :value '(round (as.numeric value) 0))
                         (dplyr/filter '(%in% text ["Almost Certainly" "Very Good Chance" "We Believe" "Likely"
                                                    "About Even" "Little Chance" "Chances Are Slight" "Almost No Chance"]))
                         (dplyr/mutate :text '(fct_reorder text value)))))

(note-void (plot->file (str target-path "d.png")
                       (r+ (gg/ggplot data (gg/aes :x 'text :y 'value :color 'text :fill 'text))
                           (gg/geom_violin :width 2.1 :size 0.2)
                           (viridis/scale_fill_viridis :discrete true)
                           (viridis/scale_color_viridis :discrete true)
                           (th/theme_ipsum_rc)
                           (gg/theme :legend.position "none")
                           (gg/coord_flip)
                           (gg/xlab "")
                           (gg/ylab "Assigned Probability (%)"))))
(note-hiccup [:image {:src "d.png"}])

(note-md "### [Violin + boxplot + sample size](https://www.r-graph-gallery.com/violin_and_boxplot_ggplot2.html)")

(note-void (def sample-size (-> data-random
                                (dplyr/group_by 'name)
                                (dplyr/summarize :num '(n)))))

(note-void (plot->file (str target-path "e.png")
                       (let [data (-> data-random
                                      (dplyr/left_join sample-size)
                                      (dplyr/mutate :myaxis '(paste0 name "\\\n" "n=" num)))]
                         (r+ (gg/ggplot data (gg/aes :x 'myaxis :y 'value :fill 'name))
                             (gg/geom_violin :width 1.4)
                             (gg/geom_boxplot :width 0.1 :color "grey" :alpha 0.2)
                             (viridis/scale_fill_viridis :discrete true)
                             (th/theme_ipsum_rc)
                             (gg/theme :legend.position "none" :plot.title (gg/element_text :size 11))
                             (gg/ggtitle "A Violing wrapping a boxplot")
                             (gg/xlab "")))))
(note-hiccup [:image {:src "e.png"}])

(note-md "### [Grouped violin chart](https://www.r-graph-gallery.com/violin_grouped_ggplot2.html)")

(note-void (def data (-> "https://raw.githubusercontent.com/holtzy/data_to_viz/master/Example_dataset/10_OneNumSevCatSubgroupsSevObs.csv"
                         (u/read-table :header true :sep ",")
                         (dplyr/mutate :tip '(round (* (/ tip total_bill) 100) 1))
                         (dplyr/mutate :day '(fct_reorder day tip))
                         (dplyr/mutate :day '(factor day :levels ["Thur" "Fri" "Sat" "Sun"])))))

(note-void (plot->file (str target-path "f.png")
                       (r+ (gg/ggplot data (gg/aes :fill 'sex :y 'tip :x 'day))
                           (gg/geom_violin :position "dodge" :alpha 0.5 :outlier.colour "transparent")
                           (viridis/scale_fill_viridis :discrete true :name "")
                           (th/theme_ipsum_rc)
                           (gg/xlab "")
                           (gg/ylab "Tip (%)")
                           (gg/ylim 0 40))))
(note-hiccup [:image {:src "f.png"}])

(note-md "## Base R and Vioplot")

(note-md "### [Vioplot package](https://www.r-graph-gallery.com/94-violin-plot.html)")

(note-void (require-r '[vioplot]))

(note-void (plot->file (str target-path "g.png")
                       (fn [] (let [treatment [(repeat 40 "A") (repeat 40 "B") (repeat 40 "C")]
                                   value [(base/sample [2 3 4 5] 40 :replace true)
                                          (base/sample [(colon 1 5) (colon 12 17)] 40 :replace true)
                                          (base/sample (colon 1 7) 40 :replace true)]
                                   data (base/data-frame :treatment treatment :value value)]
                               (base/with data '(vioplot (bra value (== treatment "A"))
                                                         (bra value (== treatment "B"))
                                                         (bra value (== treatment "C"))
                                                         :col (dev/rgb 0.1 0.4 0.7 0.7)
                                                         :names ["A" "B" "C"]))))))
(note-hiccup [:image {:src "g.png"}])

(comment (notespace.v2.note/compute-this-notespace!))
(comment (r/discard-all-sessions))

(comment (r "x11()"))
(comment (r "dev.off()"))
