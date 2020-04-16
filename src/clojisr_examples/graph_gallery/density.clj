(ns clojisr-examples.graph-gallery.density
  (:require [notespace.v2.note :as note
             :refer [note note-void note-md note-as-md note-hiccup note-as-hiccup]]
            [clojisr.v1.r :as r]))

(def target-path (notespace.v2.note/ns->out-dir *ns*))

(note-md "# [R Graph Gallery](https://www.r-graph-gallery.com/) - [Density plot](https://www.r-graph-gallery.com/density-plot.html)")
(note-md "Code from [project](https://www.r-graph-gallery.com/) by Yan Holtz")
(note-md "You can find here only translated code, please refer [original text](https://www.r-graph-gallery.com/density-plot.html)")

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
                      '[dplyr :as dplyr]
                      '[tidyr :as tidyr]
                      '[ggplot2 :as gg]
                      '[ggExtra :as gge]
                      '[viridis :as viridis]
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
(note-void (base/set-seed 7337))

(note-md "## GGPlot2")

(note-md "### [Most basic](https://www.r-graph-gallery.com/21-distribution-plot-using-ggplot2.html)")

(note-void (def data (u/read-table "https://raw.githubusercontent.com/holtzy/data_to_viz/master/Example_dataset/1_OneNum.csv"
                                   :header true)))

(note-void (plot->file (str target-path "a.png")
                       (r+ (gg/ggplot (dplyr/filter data '(< price 300)))
                           (gg/aes :x 'price)
                           (gg/geom_density :fill "#69b3a2"
                                            :color "#e9ecef"
                                            :alpha 0.8))))
(note-hiccup [:image {:src "a.png"}])

(note-md "### [Custom appearance](https://www.r-graph-gallery.com/21-distribution-plot-using-ggplot2.html)")

(note-void (plot->file (str target-path "b.png")
                       (r+ (gg/ggplot (dplyr/filter data '(< price 300)))
                           (gg/aes :x 'price)
                           (gg/geom_density :fill "#69b3a2"
                                            :color "#e9ecef"
                                            :alpha 0.8)
                           (gg/ggtitle "Night price distribution of Airbnb appartements")
                           (th/theme_ipsum_rc))))
(note-hiccup [:image {:src "b.png"}])

(note-md "### [Mirror density chart](https://www.r-graph-gallery.com/density_mirror_ggplot2.html)")

(note-void (plot->file (str target-path "c.png")
                       (let [data (base/data-frame :var1 (stats/rnorm 1000)
                                                   :var2 (stats/rnorm 1000 :mean 2))]
                         (r+ (gg/ggplot data (gg/aes :x 'x))
                             (gg/geom_density (gg/aes :x 'var1 :y '..density..) :fill "#69b3a2")
                             (gg/geom_label (gg/aes :x 4.5 :y 0.25 :label "variable1") :color "#69b3a2")
                             (gg/geom_density (gg/aes :x 'var2 :y '-..density..) :fill "#404080")
                             (gg/geom_label (gg/aes :x 4.5 :y -0.25 :label "variable2") :color "#404080")
                             (th/theme_ipsum_rc)
                             (gg/xlab "value of x")))))
(note-hiccup [:image {:src "c.png"}])
(note-md "### [Multi density chart](https://www.r-graph-gallery.com/135-stacked-density-graph.html)")

(note-void (plot->file (str target-path "d.png")
                       (r+ (gg/ggplot :data gg/diamonds (gg/aes :x 'price :group 'cut :fill 'cut))
                           (gg/geom_density :adjust 1.5)
                           (th/theme_ipsum_rc))))

(note-void (plot->file (str target-path "e.png")
                       (r+ (gg/ggplot :data gg/diamonds (gg/aes :x 'price :group 'cut :fill 'cut))
                           (gg/geom_density :adjust 1.5 :alpha 0.4)
                           (th/theme_ipsum_rc))))
(note-hiccup [:div
              [:image {:src "d.png"}]
              [:image {:src "e.png"}]])

(note-md "### [Multi density with annotation](https://www.r-graph-gallery.com/135-stacked-density-graph.html)")

(note-void (def data (-> "https://raw.githubusercontent.com/zonination/perceptions/master/probly.csv"
                         (u/read-table :header true :sep ",")
                         (tidyr/gather :key "text" :value "value")
                         (dplyr/mutate :text '(gsub "\\\\." " " text))
                         (dplyr/mutate :value '(round (as.numeric value) 0))
                         (dplyr/filter '(%in% text ["Almost No Chance" "About Even" "Probable" "Almost Certainly"])))))

(note-void (plot->file (str target-path "f.png")
                       (let [annot (base/data-frame :text ["Almost No Chance" "About Even" "Probable" "Almost Certainly"]
                                                    :x [5 53 65 79]
                                                    :y [0.15 0.4 0.06 0.1])]
                         (r+ (gg/ggplot data (gg/aes :x 'value :color 'text :fill 'text))
                             (gg/geom_density :alpha 0.6)
                             (viridis/scale_fill_viridis :discrete true)
                             (viridis/scale_color_viridis :discrete true)
                             (gg/geom_text :data annot (gg/aes :x 'x :y 'y :label 'text :color 'text)
                                           :hjust 0 :size 4.5)
                             (th/theme_ipsum_rc)
                             (gg/theme :legend.position "none")
                             (gg/xlab "")
                             (gg/ylab "Assigned Probability (%)")))))
(note-hiccup [:image {:src "f.png"}])

(note-md "### [Small multiple](https://www.r-graph-gallery.com/135-stacked-density-graph.html)")

(note-void (plot->file (str target-path "g.png")
                       (r+ (gg/ggplot :data gg/diamonds (gg/aes :x 'price :group 'cut :fill 'cut))
                           (gg/geom_density :adjust 1.5)
                           (th/theme_ipsum_rc)
                           (gg/facet_wrap ' (formula nil cut))
                           (gg/theme :legend.position "none"
                                     :panel.spacing (gg/unit 0.1 "lines")
                                     :axis.ticks.x (gg/element_blank)))))
(note-hiccup [:image {:src "g.png"}])

(note-md "### [Stacked density chart](https://www.r-graph-gallery.com/135-stacked-density-graph.html)")

(note-void (plot->file (str target-path "h.png")
                       (r+ (gg/ggplot :data gg/diamonds (gg/aes :x 'price :group 'cut :fill 'cut))
                           (gg/geom_density :adjust 1.5 :position "fill")
                           (th/theme_ipsum_rc))))
(note-hiccup [:image {:src "h.png"}])

(note-md "### [Marginal distribution](https://www.r-graph-gallery.com/277-marginal-histogram-for-ggplot2.html)")

(note (u/head mtcars))

(note-void (def marginal-plot (r+ (gg/ggplot mtcars (gg/aes :x 'wt :y 'mpg :color 'cyl :size 'cyl))
                                  (gg/geom_point)
                                  (gg/theme :legend.position "none"))))

(note-void (plot->file (str target-path "i.png")
                       (gge/ggMarginal marginal-plot :type "density")))
(note-hiccup [:image {:src "i.png"}])

(comment (notespace.v2.note/compute-this-notespace!))
(comment (r/discard-all-sessions))

(comment (r "x11()"))
(comment (r "dev.off()"))
