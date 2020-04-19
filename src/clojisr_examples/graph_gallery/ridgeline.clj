(ns clojisr-examples.graph-gallery.ridgeline
  (:require [notespace.v2.note :as note
             :refer [note note-void note-md note-as-md note-hiccup note-as-hiccup]]
            [clojisr.v1.r :as r]))

(def target-path (notespace.v2.note/ns->out-dir *ns*))

(note/defkind note-def :def {:render-src?    true
                             :value-renderer (comp notespace.v2.view/value->hiccup var-get)})

(note-md "# [R Graph Gallery](https://www.r-graph-gallery.com/) - [Ridgeline](https://www.r-graph-gallery.com/ridgeline-plot.html)")
(note-md "Code from [project](https://www.r-graph-gallery.com/) by Yan Holtz")
(note-md "You can find here only translated code, please refer [original text](https://www.r-graph-gallery.com/ridgeline.html)")

(note-md "## Setup")

(note-void (require '[clojisr.v1.r :as r :refer [r+ r* r r->clj clj->r bra colon]]
                    '[clojisr.v1.require :refer [require-r]]
                    '[clojisr.v1.applications.plotting :refer [plot->file]]))

(note-void (require-r '[base :as base :refer [$ <-]]
                      '[utils :as u]
                      '[stats :as stats]
                      '[graphics :as g]
                      '[grDevices :as dev]
                      '[dplyr :as dplyr]
                      '[tidyr :as tidyr]
                      '[ggplot2 :as gg]
                      '[ggridges :as ggr]
                      '[viridis :as viridis]
                      '[forcats]
                      '[extrafont]
                      '[hrbrthemes :as th]))

(note-md "WARNING: To use `hrbrthemes` you may want to:

1. Install Arial Narrow or Roboto Condensed fonts.
2. Register system fonts with `extrafont::font_import()` or `(r.extrafont/font_import)`
3. Fix font database as described in [here](https://github.com/hrbrmstr/hrbrthemes/issues/18#issuecomment-299692978)
4. Call `hrbrthemes::import_roboto_condensed()` or `(th/import_roboto_condensed)` 
5. Restart session")

(note-void (r.extrafont/loadfonts :quiet true))
(note-void (base/set-seed 7337))

(note-md "## GGRidges")

(note-md "### [Most basic ridgeline](https://www.r-graph-gallery.com/294-basic-ridgeline-plot.html)")

(note-void (plot->file (str target-path "a.png")
                       (r+ (gg/ggplot gg/diamonds (gg/aes :x 'price :y 'cut :fill 'cut))
                           (ggr/geom_density_ridges)
                           (ggr/theme_ridges)
                           (gg/theme :legend.position "none"))))
(note-hiccup [:image {:src "a.png"}])

(note-md "### [Distribution shape](https://www.r-graph-gallery.com/294-basic-ridgeline-plot.html#shape)")

(note-void (plot->file (str target-path "b.png")
                       (let [data (-> "https://raw.githubusercontent.com/zonination/perceptions/master/probly.csv"
                                      (u/read-table :header true :sep ",")
                                      (tidyr/gather :key "text" :value "value")
                                      (dplyr/mutate :text '(gsub "\\\\." " " text))
                                      (dplyr/mutate :value '(round (as.numeric value) 0))
                                      (dplyr/filter '(%in% text ["Almost Certainly" "Very Good Chance" "We Believe" "Likely"
                                                                 "About Even" "Little Chance" "Chances Are Slight" "Almost No Chance"]))
                                      (dplyr/mutate :text '(fct_reorder text value)))]
                         (r+ (gg/ggplot data (gg/aes :x 'value :y 'text :fill 'text))
                             (ggr/geom_density_ridges :alpha 0.6 :stat "binline" :bins 20)
                             (ggr/theme_ridges)
                             (gg/theme :legend.position "none"
                                       :panel.spacing (gg/unit 0.1 "lines")
                                       :strip.text.x (gg/element_text :size 8))
                             (gg/xlab "")
                             (gg/ylab "Assigned Probability (%)")))))
(note-hiccup [:image {:src "b.png"}])

(note-md "### [Color gradient](https://www.r-graph-gallery.com/294-basic-ridgeline-plot.html#color)")

(note-void (plot->file (str target-path "c.png")
                       (r+ (gg/ggplot ggr/lincoln_weather (gg/aes :x (symbol "`Mean Temperature [F]`")
                                                                  :y 'Month
                                                                  :fill '..x..))
                           (ggr/geom_density_ridges_gradient :scale 3 :rel_min_height 0.01)
                           (viridis/scale_fill_viridis :name "Temp. [F]" :option "C")
                           (gg/labs :title "Temperatures in Lincoln NE in 2016")
                           (th/theme_ipsum_rc)
                           (gg/theme :legend.position "none"
                                     :panel.spacing (gg/unit 0.1 "lines")
                                     :strip.text.x (gg/element_text :size 8)))))
(note-hiccup [:image {:src "c.png"}])

(comment (notespace.v2.note/compute-this-notespace!))
(comment (r/discard-all-sessions))

(comment (r "x11()"))
(comment (r "dev.off()"))
