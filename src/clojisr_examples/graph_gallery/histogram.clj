(ns clojisr-examples.graph-gallery.histogram
  (:require [notespace.v2.note :as note
             :refer [note note-void note-md note-as-md note-hiccup note-as-hiccup]]
            [clojisr.v1.r :as r]))

(def target-path (notespace.v2.note/ns->out-dir *ns*))

(note-md "# [R Graph Gallery](https://www.r-graph-gallery.com/) - [Histogram](https://www.r-graph-gallery.com/histogram.html)")
(note-md "Code from [project](https://www.r-graph-gallery.com/) by Yan Holtz")
(note-md "You can find here only translated code, please refer [original text](https://www.r-graph-gallery.com/histogram.html)")

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
(note-void (base/set-seed 7337))

(note-md "## GGPlot2")

(note-md "### [Most basic](https://www.r-graph-gallery.com/220-basic-ggplot2-histogram.html)")

(note-void (plot->file (str target-path "a.png")
                       (let [data (base/data-frame :value (stats/rnorm 100))]
                         (r+ (gg/ggplot data (gg/aes :x 'value))
                             (gg/geom_histogram)))))
(note-hiccup [:image {:src "a.png"}])

(note-md "### [Control bin size](https://www.r-graph-gallery.com/220-basic-ggplot2-histogram.html#binSize)")

(note-void (def data (u/read-table "https://raw.githubusercontent.com/holtzy/data_to_viz/master/Example_dataset/1_OneNum.csv"
                                   :header true)))

(note-void (defn bin-size [binwidth]
             (r+ (gg/ggplot (dplyr/filter data '(< price 300)))
                 (gg/aes :x 'price)
                 (gg/geom_histogram :binwidth binwidth
                                    :fill "#69b3a2"
                                    :color "#e9ecef"
                                    :alpha 0.9)
                 (gg/ggtitle (str "Bin size = " binwidth))
                 (th/theme_ipsum_rc)
                 (gg/theme :plot.title (gg/element_text :size 15)))))

(note-void (plot->file (str target-path "b.png") (bin-size 3)))
(note-void (plot->file (str target-path "c.png") (bin-size 15)))
(note-void (plot->file (str target-path "d.png") (bin-size 30)))
(note-void (plot->file (str target-path "e.png") (bin-size 100)))
(note-hiccup [:div
              [:image {:src "b.png"}]
              [:image {:src "c.png"}]])
(note-hiccup [:div
              [:image {:src "d.png"}]
              [:image {:src "e.png"}]])

(note-md "### [Mirror histogram](https://www.r-graph-gallery.com/density_mirror_ggplot2.html)")

(note-void (plot->file (str target-path "f.png")
                       (let [data (base/data-frame :var1 (stats/rnorm 1000)
                                                   :var2 (stats/rnorm 1000 :mean 2))]
                         (r+ (gg/ggplot data (gg/aes :x 'x))
                             (gg/geom_histogram (gg/aes :x 'var1 :y '..density..) :fill "#69b3a2")
                             (gg/geom_label (gg/aes :x 4.5 :y 0.25 :label "variable1") :color "#69b3a2")
                             (gg/geom_histogram (gg/aes :x 'var2 :y '-..density..) :fill "#404080")
                             (gg/geom_label (gg/aes :x 4.5 :y -0.25 :label "variable2") :color "#404080")
                             (th/theme_ipsum_rc)
                             (gg/xlab "value of x")))))
(note-hiccup [:image {:src "f.png"}])

(note-md "### [Multi histogram](https://www.r-graph-gallery.com/histogram_several_group.html)")

(note-void (plot->file (str target-path "g.png")
                       (let [data (base/data-frame :type [(repeat 1000 "variable 1")
                                                          (repeat 1000 "variable 2")]
                                                   :value [(stats/rnorm 1000)
                                                           (stats/rnorm 1000 :mean 4)])]
                         (r+ (gg/ggplot data (gg/aes :x 'value :fill 'type))
                             (gg/geom_histogram :color "#e9ecef" :alpha 0.6 :position "identity")
                             (gg/scale_fill_manual :values ["#69b3a2" "#404080"])
                             (th/theme_ipsum_rc)
                             (gg/labs :fill "")))))
(note-hiccup [:image {:src "g.png"}])

(note-md "### [Small multiple](https://www.r-graph-gallery.com/histogram_several_group.html)")

(note-void (plot->file (str target-path "h.png")
                       (let [data (-> "https://raw.githubusercontent.com/zonination/perceptions/master/probly.csv"
                                      (u/read-table :header true :sep ",")
                                      (tidyr/gather :key "text" :value "value")
                                      (dplyr/mutate :text '(gsub "\\\\." " " text))
                                      (dplyr/mutate :value '(round (as.numeric value) 0))
                                      (dplyr/mutate :text '(fct_reorder text value)))]
                         (r+ (gg/ggplot data (gg/aes :x 'value :color 'text :fill 'text))
                             (gg/geom_histogram :alpha 0.6 :binwidth 5)
                             (viridis/scale_fill_viridis :discrete true)
                             (viridis/scale_color_viridis :discrete true)
                             (th/theme_ipsum_rc)
                             (gg/theme :legend.position "none"
                                       :panel.spacing (gg/unit 0.1 "lines")
                                       :strip.text.x (gg/element_text :size 8))
                             (gg/xlab "")
                             (gg/ylab "Assigned Probability (%)")
                             (gg/facet_wrap '(formula nil text))))))
(note-hiccup [:image {:src "h.png"}])

(note-md "### [Marginal distribution](https://www.r-graph-gallery.com/277-marginal-histogram-for-ggplot2.html)")

(note (u/head mtcars))

(note-void (def marginal-plot (r+ (gg/ggplot mtcars (gg/aes :x 'wt :y 'mpg :color 'cyl :size 'cyl))
                                  (gg/geom_point)
                                  (gg/theme :legend.position "none"))))

(note-void (plot->file (str target-path "i.png")
                       (gge/ggMarginal marginal-plot :type "histogram")))
(note-hiccup [:image {:src "i.png"}])

(note-void (plot->file (str target-path "j.png")
                       (gge/ggMarginal marginal-plot :type "histogram" :size 10)))
(note-void (plot->file (str target-path "k.png")
                       (gge/ggMarginal marginal-plot :type "histogram" :fill "slateblue" :xparams {:bins 10})))

(note-hiccup [:div
              [:image {:src "j.png"}]
              [:image {:src "k.png"}]])

(note-md "## Base R")

(note-md "### [Two histograms on the same axis](https://www.r-graph-gallery.com/2-two-histograms-with-melt-colors.html)")

(note-void (def Ixos (stats/rnorm 4000 120 30)))
(note-void (def Primadur (stats/rnorm 4000 200 30)))

(note-void (plot->file (str target-path "l.png")
                       (fn []
                         (g/hist Ixos :breaks 30 :xlim [0 300] :col (dev/rgb 1 0 0 0.5) :xlab "height"
                                 :ylab "nbr of plants" :main "distribution of height of 2 durum wheat varieties")
                         (g/hist Primadur :breaks 30 :xlim [0 300] :col (dev/rgb 0 0 1 0.5) :add true)
                         (g/legend "topright" :legend ["Ixos" "Primadur"] :col [(dev/rgb 1 0 0 0.5)
                                                                                (dev/rgb 0 0 1 0.5)]
                                   :pt.cex 2 :pch 15))))
(note-hiccup [:image {:src "l.png"}])

(note-md "### [Two histograms on split window](https://www.r-graph-gallery.com/2-two-histograms-with-melt-colors.html)")

(note-void (plot->file (str target-path "m.png")
                       (fn []
                         (g/par :mfrow [1 2]
                                :mar [4 4 1 0])
                         (g/hist Ixos :breaks 30 :xlim [0 300] :col (dev/rgb 1 0 0 0.5) :xlab "height"
                                 :ylab "nbr of plants" :main "")
                         (g/hist Primadur :breaks 30 :xlim [0 300] :col (dev/rgb 0 0 1 0.5) :xlab "height"
                                 :ylab "" :main ""))))
(note-hiccup [:image {:src "m.png"}])

(note-md "### [Boxplot on the top of the histogram](https://www.r-graph-gallery.com/82-boxplot-on-top-of-histogram.html)")

(note-void (plot->file (str target-path "n.png")
                       (fn []
                         (let [my-variable [(stats/rnorm 1000 0 2)
                                            (stats/rnorm 1000 9 2)]]
                           (g/layout :mat (base/matrix [1 2] 2 1 :byrow true) :height [1 8])
                           (g/par :mar [0 3.1 1.1 2.1])
                           (g/boxplot my-variable :horizontal true :ylim [-10 20] :xaxt "n" :col (dev/rgb 0.8 0.8 0 0.5) :frame false)
                           (g/par :mar [4 3.1 1.1 2.1])
                           (g/hist my-variable :breaks 40 :col (dev/rgb 0.2 0.8 0.5 0.5)
                                   :border false :main "" :xlab "value of the variable"
                                   :xlim [-10 20])))))
(note-hiccup [:image {:src "n.png"}])

(note-md "### [Histogram with colored tail](https://www.r-graph-gallery.com/83-histogram-with-colored-tail.html)")

(note-void (plot->file (str target-path "o.png")
                       (fn []
                         (let [my-variable (stats/rnorm 2000 0 10)
                               my-hist (g/hist my-variable :breaks 40 :plot false)
                               my-color (base/ifelse (r/r< ($ my-hist 'breaks) -10)
                                                     (dev/rgb 0.2 0.8 0.5 0.5)
                                                     (base/ifelse (r/r>= ($ my-hist 'breaks) 10)
                                                                  "purple"
                                                                  (dev/rgb 0.2 0.2 0.2 0.2)))]
                           (g/plot my-hist :col my-color :border false
                                   :main "" :xlab "value of the variable"
                                   :xlim [-40 40])))))
(note-hiccup [:image {:src "o.png"}])

(note-md "### [Mirrored histogram](https://www.r-graph-gallery.com/190-mirrored-histogram.html)")

(note-void (plot->file (str target-path "p.png")
                       (fn []
                         (let [x1 (stats/rnorm 100)
                               x2 (r/r+ (stats/rnorm 100) (base/rep 2 100))]
                           (g/par :mfrow [2 1])
                           (g/par :mar [0 5 3 3])
                           (g/hist x1 :main "" :xlim [-2 5] :ylab "Frequency for x1" :xlab "" :ylim [0 25]
                                   :xaxt "n" :las 1 :col "slateblue1" :breaks 10)
                           (g/par :mar [5 5 0 3])
                           (g/hist x2 :main "" :xlim [-2 5] :ylab "Frequency for x2" :xlab "Value of my variable"
                                   :ylim [25 0] :las 1 :col "tomato3" :breaks 10)))))
(note-hiccup [:image {:src "p.png"}])

(note-md "### [Get rid of borders](https://www.r-graph-gallery.com/25-histogram-without-border.html) ")

(note-void (plot->file (str target-path "q.png")
                       (fn []
                         (let [my-variable [(stats/rnorm 1000 0 2)
                                            (stats/rnorm 1000 9 2)]]
                           (g/hist my-variable :breaks 40 :col (dev/rgb 0.2 0.8 0.5 0.5) :border false :main ""
                                   :xlab "my-variable")))))
(note-hiccup [:image {:src "q.png"}])

(comment (notespace.v2.note/compute-this-notespace!))
(comment (r/discard-all-sessions))

(comment (r "x11()"))
(comment (r "dev.off()"))
