(ns clojisr-examples.graph-gallery.boxplot
  (:require [notespace.v2.note :as note
             :refer [note note-void note-md note-as-md note-hiccup note-as-hiccup]]
            [clojisr.v1.r :as r]))

(def target-path (notespace.v2.note/ns->out-dir *ns*))

(note/defkind note-def :def {:render-src?    true
                             :value-renderer (comp notespace.v2.view/value->hiccup var-get)})

(note-md "# [R Graph Gallery](https://www.r-graph-gallery.com/) - [Boxplot](https://www.r-graph-gallery.com/boxplot.html)")
(note-md "Code from [project](https://www.r-graph-gallery.com/) by Yan Holtz")
(note-md "You can find here only translated code, please refer [original text](https://www.r-graph-gallery.com/boxplot.html)")

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

(note-md "### [Most basic boxplot](https://www.r-graph-gallery.com/262-basic-boxplot-with-ggplot2.html)")

(note-void (plot->file (str target-path "a.png")
                       (r+ (gg/ggplot mtcars (gg/aes :x '(as.factor cyl) :y 'mpg))
                           (gg/geom_boxplot :fill "slateblue" :alpha 0.2)
                           (gg/xlab "cyl"))))
(note-hiccup [:image {:src "a.png"}])

(note-md "### [geom_boxplot() options](https://www.r-graph-gallery.com/263-ggplot2-boxplot-parameters.html)")

(note-void (plot->file (str target-path "b.png")
                       (r+ (gg/ggplot gg/mpg (gg/aes :x 'class :y 'hwy))
                           (gg/geom_boxplot :color "blue" :fill "blue" :alpha 0.2 :notch true :notchwidth 0.8
                                            :outlier.color "red" :outlier.fill "red" :outlier.size 3))))
(note-hiccup [:image {:src "b.png"}])

(note-md "### [Control group order](https://www.r-graph-gallery.com/267-reorder-a-variable-in-ggplot2.html)")

(note-void (defn mpg-box [fun]
             (let [data (dplyr/mutate gg/mpg :class `(fct_reorder class hwy :.fun ~fun))]
               (r+ (gg/ggplot data (gg/aes :x 'class :y 'hwy :fill 'class))
                   (gg/geom_boxplot)
                   (gg/theme :legend.position "none")
                   (gg/xlab "")))))

(note-void (plot->file (str target-path "c.png") (mpg-box "median")))
(note-void (plot->file (str target-path "d.png") (mpg-box "length")))

(note-hiccup [:div
              [:image {:src "c.png"}]
              [:image {:src "d.png"}]])

(note-md "### [Control color](https://www.r-graph-gallery.com/264-control-ggplot2-boxplot-colors.html)")

(note-void (plot->file (str target-path "e.png")
                       (r+ (gg/ggplot gg/mpg (gg/aes :x 'class :y 'hwy))
                           (gg/geom_boxplot :color "red" :fill "orange" :alpha 0.2))))

(note-void (plot->file (str target-path "f.png")
                       (r+ (gg/ggplot gg/mpg (gg/aes :x 'class :y 'hwy :fill 'class))
                           (gg/geom_boxplot :alpha 0.3)
                           (gg/theme :legend.position "none"))))

(note-hiccup [:div
              [:image {:src "e.png"}]
              [:image {:src "f.png"}]])


(note-void (plot->file (str target-path "g.png")
                       (r+ (gg/ggplot gg/mpg (gg/aes :x 'class :y 'hwy :fill 'class))
                           (gg/geom_boxplot :alpha 0.3)
                           (gg/theme :legend.position "none")
                           (gg/scale_fill_brewer :palette "BuPu"))))

(note-void (plot->file (str target-path "h.png")
                       (r+ (gg/ggplot gg/mpg (gg/aes :x 'class :y 'hwy :fill 'class))
                           (gg/geom_boxplot :alpha 0.3)
                           (gg/theme :legend.position "none")
                           (gg/scale_fill_brewer :palette "Dark2"))))


(note-hiccup [:div
              [:image {:src "g.png"}]
              [:image {:src "h.png"}]])

(note-md "### [Highlight the group](https://www.r-graph-gallery.com/264-control-ggplot2-boxplot-colors.html#highlight)")

(note-void (plot->file (str target-path "i.png")
                       (let [data (dplyr/mutate gg/mpg :type '(ifelse (== class "subcompact") "Highlighted" "Normal"))]
                         (r+ (gg/ggplot data (gg/aes :x 'class :y 'hwy :fill 'type :alpha 'type))
                             (gg/geom_boxplot)
                             (gg/scale_fill_manual :values ["#69b3a2" "grey"])
                             (gg/scale_alpha_manual :values [1 0.1])
                             (th/theme_ipsum_rc)
                             (gg/theme :legend.position "none")
                             (gg/xlab "")))))
(note-hiccup [:image {:src "i.png"}])

(note-md "### [Grouped boxplot](https://www.r-graph-gallery.com/265-grouped-boxplot-with-ggplot2.html)")

(note-void (def data (let [variety (base/rep (bra 'LETTERS (colon 1 7)) :each 40)
                           treatment (base/rep ["high" "low"] :each 20)
                           note (r+ (base/seq (colon 1 280))
                                    (base/sample (colon 1 150) 280 :replace true))]
                       (base/data-frame :variety variety
                                        :treatment treatment
                                        :note note))))

(note-void (plot->file (str target-path "j.png")
                       (r+ (gg/ggplot data (gg/aes :x 'variety :y 'note :fill 'treatment))
                           (gg/geom_boxplot))))
(note-hiccup [:image {:src "j.png"}])

(note-md "### [Faceting in boxplot](https://www.r-graph-gallery.com/265-grouped-boxplot-with-ggplot2.html)")

(note-void (plot->file (str target-path "k.png")
                       (r+ (gg/ggplot data (gg/aes :x 'variety :y 'note :fill 'treatment))
                           (gg/geom_boxplot)
                           (gg/facet_wrap '(formula nil treatment)))))

(note-void (plot->file (str target-path "l.png")
                       (r+ (gg/ggplot data (gg/aes :x 'variety :y 'note :fill 'treatment))
                           (gg/geom_boxplot)
                           (gg/facet_wrap '(formula nil variety) :scale "free"))))

(note-hiccup [:div
              [:image {:src "k.png"}]
              [:image {:src "l.png"}]])

(note-md "### [Variable width](https://www.r-graph-gallery.com/266-ggplot2-boxplot-with-variable-width.html)")

(note-void (plot->file (str target-path "m.png")
                       (let [names [(repeat 20 "A") (repeat 5 "B") (repeat 30 "C") (repeat 100 "D")]
                             value [(base/sample [2 3 4 5] 20 :replace true)
                                    (base/sample [4 5 6 7 8 9 10] 5 :replace true)
                                    (base/sample [1 2 3 4 5 6 7] 30 :replace true)
                                    (base/sample [3 4 5 6 7 8] 100 :replace true)]
                             data (base/data-frame :names names :value value)
                             my-xlab (base/paste (base/levels ($ data 'names)) "\\n(N=" (base/table ($ data 'names)) ")" :sep "")]
                         (r+ (gg/ggplot data (gg/aes :x 'names :y 'value :fill 'names))
                             (gg/geom_boxplot :varwidth true :alpha 0.2)
                             (gg/theme :legend.position "none")
                             (gg/scale_x_discrete :labels my-xlab)))))
(note-hiccup [:image {:src "m.png"}])

(note-md "### [Boxplot from continuous variable](https://www.r-graph-gallery.com/268-ggplot2-boxplot-from-continuous-variable.html)")

(note-void (plot->file (str target-path "n.png")
                       (let [data (dplyr/mutate gg/diamonds :bin '(cut_width carat :width 0.5 :boundary 0))]
                         (r+ (gg/ggplot data (gg/aes :x 'bin :y 'price))
                             (gg/geom_boxplot :fill "#69b3a2")
                             (th/theme_ipsum_rc)
                             (gg/xlab "Carat")))))
(note-hiccup [:image {:src "n.png"}])

(note-md "### [Add mean value](https://www.r-graph-gallery.com/269-ggplot2-boxplot-with-average-value.html)")

(note-void (plot->file (str target-path "o.png")
                       (let [names [(repeat 20 "A") (repeat 8 "B") (repeat 30 "C") (repeat 80 "D")]
                             value [(base/sample [2 3 4 5] 20 :replace true)
                                    (base/sample [4 5 6 7 8 9 10] 8 :replace true)
                                    (base/sample [1 2 3 4 5 6 7] 30 :replace true)
                                    (base/sample [3 4 5 6 7 8] 80 :replace true)]
                             data (base/data-frame :names names :value value)]
                         (r+ (gg/ggplot data (gg/aes :x 'names :y 'value :fill 'names))
                             (gg/geom_boxplot :alpha 0.7)
                             (gg/stat_summary :fun.y 'mean :geom "point" :shape 20 :size 14 :color "red" :fill "red")
                             (gg/theme :legend.position "none")))))
(note-hiccup [:image {:src "o.png"}])

(note-md "### [Add individual observation](https://www.r-graph-gallery.com/89-box-and-scatter-plot-with-ggplot2.html)")

(note-void (plot->file (str target-path "p.png")
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
                             (gg/geom_boxplot)
                             (viridis/scale_fill_viridis :discrete true :alpha 0.6)
                             (gg/geom_jitter :color "black" :size 0.4 :alpha 0.9)
                             (th/theme_ipsum_rc)
                             (gg/theme :legend.position "none" :plot.title (gg/element_text :size 11))
                             (gg/ggtitle "A boxplot with jitter")
                             (gg/xlab "")))))
(note-hiccup [:image {:src "p.png"}])

(note-md "### [Marginal distribution](https://www.r-graph-gallery.com/277-marginal-histogram-for-ggplot2.html)")

(note (u/head mtcars))

(note-void (def marginal-plot (r+ (gg/ggplot mtcars (gg/aes :x 'wt :y 'mpg :color 'cyl :size 'cyl))
                                  (gg/geom_point)
                                  (gg/theme :legend.position "none"))))

(note-void (plot->file (str target-path "q.png")
                       (gge/ggMarginal marginal-plot :type "boxplot")))
(note-hiccup [:image {:src "q.png"}])

(note-md "## Base R")

(note-md "### [Boxplot on the top of the histogram](https://www.r-graph-gallery.com/82-boxplot-on-top-of-histogram.html)")

(note-void (plot->file (str target-path "r.png")
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
(note-hiccup [:image {:src "r.png"}])

(note-md "### [Boxplot with custom colors](https://www.r-graph-gallery.com/23-add-colors-to-specific-groups-of-a-boxplot.html)")

(note-void (plot->file (str target-path "s.png")
                       #(let [names [(repeat 20 "Maestro")
                                     (repeat 20 "Presto")
                                     (repeat 20 "Nerak")
                                     (repeat 20 "Eskimo")
                                     (repeat 20 "Nairobi")
                                     (repeat 20 "Artiko")]
                              value [(base/sample (colon 3 10) 20 :replace true)
                                     (base/sample (colon 2 5) 20 :replace true)
                                     (base/sample (colon 6 10) 20 :replace true)
                                     (base/sample (colon 6 10) 20 :replace true)
                                     (base/sample (colon 1 7) 20 :replace true)
                                     (base/sample (colon 3 10) 20 :replace true)]
                              data (base/data-frame :names names :value value)
                              my-colors (base/ifelse (r/r== (base/levels ($ data 'names)) "Nairobi")
                                                     (dev/rgb 0.1 0.1 0.7 0.5)
                                                     (base/ifelse (r/r== (base/levels ($ data 'names)) "Eskimo")
                                                                  (dev/rgb 0.8 0.1 0.3 0.6)
                                                                  "grey90"))]
                          (g/boxplot `(formula ($ ~data value) ($ ~data names))
                                     :col my-colors :ylab "disease" :xlab " - variety -")
                          (g/legend "bottomleft" :legend ["Positive control" "Negative control"]
                                    :col [(dev/rgb 0.1 0.1 0.7 0.5)
                                          (dev/rgb 0.8 0.1 0.3 0.6)]
                                    :bty "n" :pch 20 :pt.cex 3 :cex 1 :horiz false :inset [0.03 0.1]))))
(note-hiccup [:image {:src "s.png"}])

(note-md "### [X axis labels on several lines](https://www.r-graph-gallery.com/70-boxplot-with-categories-on-multiple-lines.html)")
(note-void (plot->file (str target-path "t.png")
                       #(let [a (base/sample (colon 2 24) 20 :replace true)
                              b (base/sample (colon 4 14) 20 :replace true)
                              C (base/names<- [:!list a b]
                                              [(base/paste "Category 1\\n n=" (base/length a))
                                               (base/paste "Category 2\\n n=" (base/length b))])]
                          (g/par :mgp [3 2 0])
                          (g/boxplot C :col "#69b3a2" :ylab "value"))))
(note-hiccup [:image {:src "t.png"}])

(note-md "### [Boxplot with jitter](https://www.r-graph-gallery.com/96-boxplot-with-jitter.html)")

(note-void (plot->file (str target-path "u.png")
                       #(let [names [(repeat 80 "A") (repeat 50 "B") (repeat 70 "C")]
                              value [(stats/rnorm 80 :mean 10 :sd 9)
                                     (stats/rnorm 50 :mean 2 :sd 15)
                                     (stats/rnorm 70 :mean 30 :sd 10)]
                              data (base/data-frame :names names :value value)
                              mylevels (base/levels ($ data 'names))
                              level-proportions (r/rdiv (base/summary ($ data 'names))
                                                        (base/nrow data))]
                          (g/boxplot `(formula ($ ~data value) ($ ~data names))
                                     :col (dev/terrain-colors 4)
                                     :xlab "" :ylab "")
                          (r `(for [i (colon 1 (length ~mylevels))]
                                ~(let [thislevel `(bra ~mylevels i)
                                       thisvalues `(bra ~data (== ($ ~data names) ~thislevel) "value")
                                       myjitter `(jitter (rep i (length ~thisvalues)) :amount (/ (bra ~level-proportions i) 2))]
                                   `(points ~myjitter ~thisvalues :pch 20 :col (rgb 0 0 0 0.9))))))))
(note-hiccup [:image {:src "u.png"}])

(note-md "### [Order categories by median](https://www.r-graph-gallery.com/9-ordered-boxplot.html)")

(note-void (plot->file (str target-path "v.png")
                       #(let [variety (base/rep ["soldur", "silur", "lloyd", "pescadou", "X4582", "Dudur", "Classic"] :each 20)
                              note [(base/sample (colon 2 5) 20 :replace true)
                                    (base/sample (colon 6 10) 20 :replace true)
                                    (base/sample (colon 1 7) 30 :replace true)
                                    (base/sample (colon 3 10) 70 :replace true)]
                              data (base/data-frame :variety variety :note note)
                              new-order (base/with data `(reorder variety note median :na.rm true))]
                          (g/boxplot `(formula ($ ~data note) ~new-order)
                                     :ylab "sickness" :col "#69b3a2" :xlab "" :boxwex 0.4 :main ""))))
(note-hiccup [:image {:src "v.png"}])

(note-md "### [Boxplot with specific order](https://www.r-graph-gallery.com/22-order-boxplot-labels-by-names.html)")

(note-void (plot->file (str target-path "w.png")
                       #(let [names [(repeat 20 "A")
                                     (repeat 20 "B")
                                     (repeat 20 "C")
                                     (repeat 20 "D")]
                              value [(base/sample (colon 2 5) 20 :replace true)
                                     (base/sample (colon 6 10) 20 :replace true)
                                     (base/sample (colon 1 7) 20 :replace true)
                                     (base/sample (colon 3 10) 20 :replace true)]
                              data (base/$<- (base/data-frame :names names :value value) 'names
                                             (base/factor names :levels ["A" "D" "C" "B"]))]
                          (g/boxplot `(formula ($ ~data value)
                                               ($ ~data names))
                                     :col (dev/rgb 0.3 0.5 0.4 0.6)
                                     :ylab "value"
                                     :xlab "names in desired order"))))
(note-hiccup [:image {:src "w.png"}])

(note-md "### [Grouped and ordered boxplot](https://www.r-graph-gallery.com/9-ordered-boxplot.html#grouped)")

(note-void (plot->file (str target-path "x.png")
                       ;; Create dummy data
                       #(let [variety (base/rep ["soldur", "silur", "lloyd", "pescadou", "X4582", "Dudur", "Classic"] :each 40)
                              treatment (base/rep [(repeat 20 "high") (repeat 20 "low")] 7)
                              note [(base/rep [(base/sample (colon 0 4) 20 :replace true)
                                               (base/sample (colon 1 6) 20 :replace true)] 2) 
                                    (base/rep [(base/sample (colon 5 7) 20 :replace true)
                                               (base/sample (colon 5 9) 20 :replace true)] 2) 
                                    [(base/sample (colon 0 4) 20 :replace true)
                                     (base/sample (colon 2 5) 20 :replace true), 
                                     (base/rep [(base/sample (colon 6 8) 20 :replace true)
                                                (base/sample (colon 7 10) 20 :replace true)] 2)]]
                              data (base/data-frame :variety variety :treatment treatment :note note)
                              ;; Reorder varieties (group) (mixing low and high treatments for the calculations)
                              new-order (base/with data '(reorder variety note mean :na.rm true))]
                          ;; Then I make the boxplot, asking to use the 2 factors : variety (in the good order) AND treatment
                          (g/par :mar [3 4 3 1])
                          (let [myplot (g/boxplot `(formula note (* treatment ~new-order))
                                                  :data data :boxwex 0.4 :ylab "sickness"
                                                  :main "sickness of several wheat lines"
                                                  :col ["slateblue1" "tomato"] 
                                                  :xaxt "n")
                                ;; To add the label of x axis
                                my-names (base/sapply (base/strsplit ($ myplot 'names) "\\\\.") '(function [x] (brabra x 2)))
                                my-names (bra my-names (base/seq 1 (base/length my-names) 2))]
                            (g/axis 1 :at (base/seq 1.5 14 2)
                                    :labels my-names
                                    :tick false :cex 0.3)
                            ;; Add the grey vertical lines
                            (doseq [i (range 0.5 20 2)]
                              (g/abline :v i :lty 1 :col "gray"))
                            ;; Add a legend
                            (g/legend "bottomright" :legend ["High treatment" "Low treatment"]
                                      :col ["slateblue1" "tomato"]
                                      :pch 15 :bty "n" :pt.cex 3 :cex 1.2 :horiz false :inset [0.1 0.1])))))
(note-hiccup [:image {:src "x.png"}])

(note-md "### [Boxplot with variable width](https://www.r-graph-gallery.com/24-boxplot-with-variable-width.html)")

(note-void (plot->file (str target-path "y.png")
                       #(let [names [(repeat 20 "A") (repeat 8 "B") (repeat 30 "C") (repeat 80 "D")]
                              value [(base/sample (colon 2 5) 20 :replace true)
                                     (base/sample (colon 4 10) 8 :replace true)
                                     (base/sample (colon 1 7) 30 :replace true)
                                     (base/sample (colon 3 8) 80 :replace true)]
                              data (base/data-frame :names names :value value)
                              proportion (r/rdiv (base/table ($ data 'names)) (base/nrow data))]
                          (g/boxplot `(formula ($ ~data value)
                                               ($ ~data names))
                                     :width proportion
                                     :col ["orange" "seagreen"]
                                     :xlab "names" :ylab "value"))))
(note-hiccup [:image {:src "y.png"}])

(note-md "### [Boxplot with labels on top](https://www.r-graph-gallery.com/26-add-text-over-a-boxplot.html)")

(note-void (plot->file (str target-path "z.png")
                       #(let [names [(repeat 20 "A") (repeat 8 "B") (repeat 30 "C") (repeat 80 "D")]
                              value [(base/sample (colon 2 5) 20 :replace true)
                                     (base/sample (colon 4 10) 8 :replace true)
                                     (base/sample (colon 1 7) 30 :replace true)
                                     (base/sample (colon 3 8) 80 :replace true)]
                              data (base/data-frame :names names :value value)
                              boundaries (g/boxplot `(formula ($ ~data value)
                                                              ($ ~data names)) :col "#69b3a2" :ylim [1 11]
                                                    :xlab "names" :ylab "value")
                              nb-group (base/nlevels ($ data 'names))]
                          (g/text :x [(colon 1 nb-group)]
                                  :y (r+ (bra ($ boundaries 'stats) (base/nrow ($ boundaries 'stats)) nil) 0.5)
                                  (base/paste "n = " (base/table ($ data 'names)) :sep "")))))
(note-hiccup [:image {:src "z.png"}])

(note-md "### [Tukey test](https://www.r-graph-gallery.com/84-tukey-test.html)")

(note-void (require-r '[multcompView :as mcv]))
(note-void (base/set-seed 1))

(note-void (def data (let [treatment (base/rep ["A" "B" "C" "D" "E"] :each 20)
                           value [(base/sample (colon 2 5) 20 :replace true)
                                  (base/sample (colon 6 10) 20 :replace true)
                                  (base/sample (colon 1 7) 20 :replace true)
                                  (base/sample (colon 3 10) 20 :replace true)
                                  (base/sample (colon 10 20) 20 :replace true)]]
                       (<- 'data (base/data-frame :treatment treatment :value value)))))

(note-def (def model (stats/lm '(formula ($ data value)
                                         ($ data treatment)))))

(note-def (def ANOVA (stats/aov model)))

(note-def (def TUKEY (stats/TukeyHSD :x ANOVA "data$treatment" :conf.level 0.95)))

(note-void (plot->file (str target-path "A.png")
                       #(g/plot TUKEY :las 1 :col "brown")))
(note-hiccup [:image {:src "A.png"}])

(note-void (def generate-label-df (r '(function [TUKEY variable]
                                                (<- Tukey.levels (bra (brabra TUKEY variable) nil 4))
                                                (<- Tukey.labels (data.frame (bra (multcompLetters Tukey.levels) "Letters")))
                                                (<- Tukey.labels (~(symbol "`$<-`") Tukey.labels treatment (rownames Tukey.labels)))
                                                (bra Tukey.labels (order Tukey.labels$treatment) nil)))))

(note-def (def LABELS (generate-label-df TUKEY "data$treatment")))
(note-void (def my-colors [(dev/rgb 143 199 74 :maxColorValue 255)
                           (dev/rgb 242 104 34 :maxColorValue 255)
                           (dev/rgb 111 145 202 :maxColorValue 255)]))

(note-void (plot->file (str target-path "B.png")
                       #(let [a (g/boxplot '(formula data$value data$treatment)
                                           :ylim [(base/min 'data$value)
                                                  (r/r* 1.1 (base/max 'data$value))]
                                           :col (bra my-colors (base/as-numeric (bra LABELS nil 1)))
                                           :ylab "value" :main "")
                              over (r/r* 0.1 (base/max (bra ($ a 'stats) (base/nrow ($ a 'stats)) nil)))]
                          (g/text [(colon 1 (base/nlevels 'data$treatment))]
                                  (r+ (bra ($ a 'stats) (base/nrow ($ a 'stats)) nil) over)
                                  (bra LABELS nil 1)
                                  :col (bra my-colors (base/as-numeric (bra LABELS nil 1)))))))
(note-hiccup [:image {:src "B.png"}])

(note-md "### [Box type around plot](https://www.r-graph-gallery.com/73-box-style-with-the-bty-function.html)")

(note-void (plot->file (str target-path "C.png")
                       #(let [a (r+ (base/seq 1 29)
                                    (r/r* 4 (stats/runif 29 0.4)))
                              b (r+ (r/r** (base/seq 1 29) 2)
                                    (stats/runif 29 0.98))]
                          (g/par :mfrow [2 2])
                          (g/par :bty "l")
                          (g/boxplot a :col "#69b3a2" :xlab "bottom & left box")
                          (g/par :bty "o")
                          (g/boxplot b :col "#69b3a2" :xlab "complete box" :horizontal true)
                          (g/par :bty "c")
                          (g/boxplot a :col "#69b3a2" :xlab "up & bottom & left box" :width 0.5)
                          (g/par :bty "n")
                          (g/boxplot a :col "#69b3a2" :xlab "no box"))))
(note-hiccup [:image {:src "C.png"}])

(note-void "### [Split plot window with layout()](https://www.r-graph-gallery.com/75-split-screen-with-layout.html)")

(note-void "#### Two rows")

(note-void (plot->file (str target-path "D.png")
                       #(let [a (r+ (base/seq 1 129)
                                    (r/r* 4 (stats/runif 129 0.4)))
                              b (r+ (r/r** (base/seq 1 129) 2)
                                    (stats/runif 129 0.98))
                              nf (g/layout (base/matrix [1 2] :ncol 1))]
                          (g/hist a :breaks 30 :border false :col (dev/rgb 0.1 0.8 0.3 0.5) :xlab "distribution of a" :main "")
                          (g/boxplot a :col (dev/rgb 0.8 0.8 0.3 0.5) :xlab "a" :las 2))))
(note-hiccup [:image {:src "D.png"}])

(note-void "#### Two columns")

(note-void (plot->file (str target-path "E.png")
                       #(let [a (r+ (base/seq 1 129)
                                    (r/r* 4 (stats/runif 129 0.4)))
                              b (r+ (r/r** (base/seq 1 129) 2)
                                    (stats/runif 129 0.98))
                              nf (g/layout (base/matrix [1 2] :ncol 2))]
                          (g/hist a :breaks 30 :border false :col (dev/rgb 0.1 0.8 0.3 0.5) :xlab "distribution of a" :main "")
                          (g/boxplot a :col (dev/rgb 0.8 0.8 0.3 0.5) :xlab "a" :las 2))))
(note-hiccup [:image {:src "E.png"}])

(note-void "#### Subdivide second row")

(note-void (plot->file (str target-path "F.png")
                       #(let [a (r+ (base/seq 1 129)
                                    (r/r* 4 (stats/runif 129 0.4)))
                              b (r+ (r/r** (base/seq 1 129) 2)
                                    (stats/runif 129 0.98))
                              nf (g/layout (base/matrix [1 1 2 3] :nrow 2 :byrow true))]
                          (g/hist a :breaks 30 :border false :col (dev/rgb 0.1 0.8 0.3 0.5) :xlab "distribution of a" :main "")
                          (g/boxplot a :col (dev/rgb 0.8 0.8 0.3 0.5) :xlab "a" :las 2)
                          (g/boxplot b :col (dev/rgb 0.4 0.2 0.3 0.5) :xlab "b" :las 2))))
(note-hiccup [:image {:src "F.png"}])

(note-void "#### Subdivide second row")

(note-void (plot->file (str target-path "G.png")
                       #(let [a (r+ (base/seq 1 129)
                                    (r/r* 4 (stats/runif 129 0.4)))
                              b (r+ (r/r** (base/seq 1 129) 2)
                                    (stats/runif 129 0.98))
                              nf (g/layout (base/matrix [1 1 2 3] :nrow 2 :byrow true)
                                           :widths [3 1]
                                           :heights [2 2])]
                          (g/hist a :breaks 30 :border false :col (dev/rgb 0.1 0.8 0.3 0.5) :xlab "distribution of a" :main "")
                          (g/boxplot a :col (dev/rgb 0.8 0.8 0.3 0.5) :xlab "a" :las 2)
                          (g/boxplot b :col (dev/rgb 0.4 0.2 0.3 0.5) :xlab "b" :las 2))))
(note-hiccup [:image {:src "G.png"}])


(comment (notespace.v2.note/compute-this-notespace!))
(comment (r/discard-all-sessions))

(comment (r "x11()"))
(comment (r "dev.off()"))
