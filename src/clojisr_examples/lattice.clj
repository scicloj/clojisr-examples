(ns clojisr-examples.lattice
  "Rework of examples from Lattice book.

  http://lmdvr.r-forge.r-project.org/figures/figures.html"
  (:require [clojisr.v1.r :as r :refer [r+ r r->clj clj->r clj->java java->clj java->r r->java]]
            [clojisr.v1.require :refer [require-r]]))

;; Be sure to install following packages:
;;
;; install.packages(c("lattice", "latticeExtra", "mlmrev", "MEMSS", "locfit", "hexbin"))

(require-r '[lattice :as lat]
           '[latticeExtra :as late]
           '[stats :as stats]
           '[mlmRev]
           '[MEMSS]
           '[grDevices :as dev]
           '[graphics :as g]
           '[base :refer [$ $<- <- summary] :as base]
           '[utils]
           '[grid]
           '[locfit]
           '[hexbin])

(require-r '[datasets :refer :all])

(r->clj r.base/version)
;; => {:platform ["x86_64-pc-linux-gnu"],
;;     :arch ["x86_64"],
;;     :os ["linux-gnu"],
;;     :system ["x86_64, linux-gnu"],
;;     :status [""],
;;     :major ["3"],
;;     :minor ["6.2"],
;;     :year ["2019"],
;;     :month ["12"],
;;     :day ["12"],
;;     :svn rev ["77560"],
;;     :language ["R"],
;;     :version.string ["R version 3.6.2 (2019-12-12)"],
;;     :nickname ["Dark and Stormy Night"]}

;;

;; Helper to build formulas
(defmacro formula [& frm] `(r ~(apply str frm)))

;; Define variable on both sides (R and Clojure)
(defmacro def-r [name & r]
  `(do
     (def ~name ~@r)
     (<- '~(symbol name) ~name)))

;; Construct R symbol from a string
(defmacro rsymbol [s] `(symbol ~(format "`%s`" (name s))))

;;

(dev/x11 :width 9)

;; Chapter 1

(def chem97 r.mlmRev/Chem97)

(def gcse-formula (formula "~" gcsescore | factor (score)))
;; => #'generateme/gcse-formula

(r->clj (stats/xtabs (r "~ score") :data chem97))
;; => [3688 3627 4619 5739 6668 6681]

;; Figure 1

(-> gcse-formula
    (lat/histogram :data chem97))

;; Figure 2

(-> gcse-formula
    (lat/densityplot :data chem97 :plot.points false :ref true))

;; Figure 3

(-> (formula "~" gcsescore)
    (lat/densityplot :data chem97 :groups 'score :plot.points false
                     :ref true :auto.key {:columns 3}))

;; Figure 4

(def tp1 (-> gcse-formula
             (lat/histogram :data chem97)))

(def tp2 (-> (formula "~" gcsescore)
             (lat/densityplot :data chem97 :groups 'score :plot.points false
                              :auto.key {:space "right" :title "score"})))

(base/class tp2)
;; => [1] "trellis"

(r->clj (base/summary tp1))
;; => {:call
;;     [histogram [$ .MEM x481db63caee84aa6] [$ .MEM xad4e82ae81ba44aa]],
;;     :packet.sizes [3688.0 3627.0 4619.0 5739.0 6668.0 6681.0],
;;     :index.cond [[1 2 3 4 5 6]],
;;     :perm.cond [1]}

(do
  (g/plot tp1 :split [1 1 1 2])
  (g/plot tp2 :split [1 2 1 2] :newpage false))

;; Chapter 2

(def oats r.MEMSS/Oats)

;; Figure 1

(def tp1-oats (-> (formula yield "~" nitro | Variety + Block)
                  (lat/xyplot :data oats :type "o")))

(base/print tp1-oats)

(base/dim tp1-oats)
;; => [1] 3 6

(r->clj (base/dimnames tp1-oats))
;; => {:Variety ["Golden Rain" "Marvellous" "Victory"], :Block ["I" "II" "III" "IV" "V" "VI"]}

(stats/xtabs (formula "~" Variety + Block) :data oats)
;; =>              Block
;; Variety       I II III IV V VI
;; Golden Rain 4  4   4  4 4  4
;; Marvellous  4  4   4  4 4  4
;; Victory     4  4   4  4 4  4

(r->clj (base/summary tp1-oats))
;; => {:call
;;     [xyplot
;;      [$ .MEM xf2107be653ea406e]
;;      [$ .MEM xf0c8524fef974c91]
;;      [$ .MEM xedf74c54a4df4332]],
;;     :packet.sizes
;;     [4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0],
;;     :index.cond [[1 2 3] [1 2 3 4 5 6]],
;;     :perm.cond [1 2]}

(r->clj (base/summary (r/bra tp1-oats (r/empty-symbol) 1)))
;; => {:call
;;     [xyplot
;;      [$ .MEM xf2107be653ea406e]
;;      [$ .MEM xf0c8524fef974c91]
;;      [$ .MEM xedf74c54a4df4332]
;;      new.levs],
;;     :packet.sizes
;;     [4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0
;;      4.0],
;;     :index.cond [[1 2 3] [1]],
;;     :perm.cond [1 2]}

;; Figure 2

(base/print (r/bra tp1-oats (r/empty-symbol) 1))

;; Figure 3

(stats/update tp1-oats :aspect "xy")

;; Figure 4

(stats/update tp1-oats :aspect "xy" :layout [0 18])

;; Figure 5

(stats/update tp1-oats :aspect "xy" :layout [0 18] :between {:x [0 0 0.5] :y 0.5})

;; Figure 6

(-> (formula variety "~" yield | site)
    (lat/dotplot lat/barley :layout [1 6] :aspect 0.7 :groups 'year :auto.key {:space "right"}))

;; Figure 7

(def key-variety
  {:space "right"
   :text (r->clj (base/list (base/levels ($ oats 'Variety))))
   :points {:pch (range 1 3) :col "black"}})

(-> (formula yield "~" nitro | Block)
    (lat/xyplot oats :aspect "xy" :type "o" :groups 'Variety
                :key key-variety :lty 1 :pch (range 1 3)
                :col.line "darkgrey" :col.symbol "black"
                :xlab "Nitrogen concentration (cwt/acre)"
                :ylab "Yield (bushels/acre)"
                :main "Yield of three varieties of oats"
                :sub "A 3 x 4 split plot experiment with 6 blocks"))

;; Figure 8

(-> (formula Class "~" Freq | Sex + Age)
    (lat/barchart :data (base/as-data-frame Titanic)
                  :groups 'Survived :stack true :layout [4 1]
                  :auto.key {:title "Survived" :columns 2}))


;; Figure 9

(-> (formula Class "~" Freq | Sex + Age)
    (lat/barchart :data (base/as-data-frame Titanic)
                  :groups 'Survived :stack true :layout [4 1]
                  :auto.key {:title "Survived" :columns 2}
                  :scales {:x "free"}))

;; Figure 10

(def bc-titanic (-> (formula Class "~" Freq | Sex + Age)
                    (lat/barchart :data (base/as-data-frame Titanic)
                                  :groups 'Survived :stack true :layout [4 1]
                                  :auto.key {:title "Survived" :columns 2}
                                  :scales {:x "free"})))

(stats/update bc-titanic :panel '(function [...]
                                           (panel.grid :h 0 :v -1)
                                           (panel.barchart ...)))

;; Figure 11

(stats/update bc-titanic :panel '(function [... border]
                                           (panel.barchart ... :border "transparent")))

;; Chapter 3

;; Figure 1

(-> (formula "~ eruptions")
    (lat/densityplot :data faithful))

;; Figure 2

(-> (formula "~ eruptions")
    (lat/densityplot :data faithful :kernel "rect" :bw 0.2 :plot.points "rug" :n 200))

;; Figure 3

(-> (formula "~" log (FSC.H) | Days)
    (lat/densityplot :data late/gvhd10 :plot.points false :ref true :layout [2 4]))

;; Figure 4

(-> (formula "~" log2 (FSC.H) | Days)
    (lat/histogram :data late/gvhd10 :xlab "log Forward Scatter"
                   :type "density" :nint 50 :layout [2 4]))


;; Figure 5

(-> (formula "~" gcsescore | factor (score))
    (lat/qqmath :data chem97 :f.value (stats/ppoints 100)))

;; Figure 6

(-> (formula "~" gcsescore | gender)
    (lat/qqmath chem97 :groups 'score :aspect "xy" :f.value (stats/ppoints 100)
                :auto.key {:space "right"}
                :xlab "Standard Normal Quantiles", 
                :ylab "Average GCSE Score"))

;; Figure 7

(def chem97-mod (base/transform chem97 :gcsescore.trans [(rsymbol "^") 'gcsescore 2.34]))

(-> (formula "~" gcsescore.trans | gender)
    (lat/qqmath chem97-mod :groups 'score :aspect "xy" :f.value (stats/ppoints 100)
                :auto.key {:space "right" :title "score"}
                :xlab "Standard Normal Quantiles", 
                :ylab "Transformed GCSE Score"))

;; Figure 8

(-> (formula "~" gcsescore | factor (score))
    (late/ecdfplot :data chem97 :groups 'gender :auto.key {:columns 2}
                   :subset '(> gcsescore 0)
                   :xlab "Average GCSE Score"))

;; Figure 9

(-> (formula "~" gcsescore | factor (score))
    (lat/qqmath :data chem97 :groups 'gender :auto.key {:columns 2 :points false :lines true}
                :type "l" :distribution 'qunif
                :prepanel 'prepanel.qqmathline :aspect "xy"
                :subset '(> gcsescore 0)
                :xlab "Standard Normal Quantiles", 
                :ylab "Average GCSE Score"))

;; Figure 10

(-> (formula gender "~" gcsescore | factor (score))
    (lat/qq :data chem97 :f.value (stats/ppoints 100) :aspect 1))

;; Figure 11

(-> (formula factor (score) "~" gcsescore | gender)
    (lat/bwplot :data chem97 :xlab "Average GCSE Score"))

;; Figure 12

(-> (formula gcsescore "^" 2.34 "~" gender | factor(score))
    (lat/bwplot :data chem97 :varwidth true :layout [6 1]
                :xlab "Average GCSE Score"))

;; Figure 13

(-> (formula Days "~" log (FSC.H))
    (lat/bwplot :data late/gvhd10
                :xlab "log(Forward Scatter)"
                :ylab "Days Past Transplant"))


;; Figure 14

(-> (formula Days "~" log (FSC.H))
    (lat/bwplot :data late/gvhd10 :panel 'panel.violin :box.ratio 3
                :xlab "log(Forward Scatter)"
                :ylab "Days Past Transplant"))

;; Figure 15

(-> (formula factor (mag) "~" depth)
    (lat/stripplot quakes))

;; Figure 16

(-> (formula depth "~" factor (mag))
    (lat/stripplot quakes :jitter.data true :alpha 0.6
                   :xlab "Magnitude (Richter)" :ylab "Depth (km)"))

;; Figure 17

(-> (r "sqrt(abs(residuals(lm(yield~variety+year+site)))) ~ site")
    (lat/stripplot :data lat/barley :groups 'year :jitter.data true
                   :auto.key {:points true :lines true :columns 2}
                   :type ["p" "a"] :fun 'median
                   :ylab (r "expression(abs('Residual Barley Yield')^{1 / 2})")))

;; Chapter 4

(r->clj VADeaths)
;; => [11.7 18.1 26.9 41.0 66.0 8.7 11.7 20.3 30.9 54.3 15.4 24.3 37.0 54.6 71.1 8.4 13.6 19.3 35.1 50.0]

(base/class VADeaths)
;; => [1] "matrix"

(r->clj (r.utils/methods "dotplot"))
;; => ["dotplot.array" "dotplot.coef.mer" "dotplot.default" "dotplot.formula" "dotplot.matrix" "dotplot.numeric" "dotplot.ranef.mer" "dotplot.table"]

;; Figure 1

(lat/dotplot VADeaths :groups false)

;; Figure 2

(lat/dotplot VADeaths :groups false
             :layout [1 4] :aspect 0.7
             :origin 0 :type ["p" "h"]
             :main "Death Rates in Virginia - 1940" 
             :xlab "Rate (per 1000)")

;; Figure 3

(lat/dotplot VADeaths, :type "o"
             :auto.key {:lines true :space "right"}
             :main "Death Rates in Virginia - 1940"
             :xlab "Rate (per 1000)")

;; Figure 4

(lat/barchart VADeaths :groups false
              :layout [1 4] :aspect 0.7
              :reference false
              :main "Death Rates in Virginia - 1940" 
              :xlab "Rate (per 1000)")

;; Figure 5

(-> (base/prop-table late/postdoc :margin 1)
    (lat/barchart :xlab "Proportion"
                  :auto.key {:adj 1}))

;; Figure 6

(-> (base/prop-table late/postdoc :margin 1)
    (lat/dotplot :groups false
                 :xlab "Proportion"
                 :par.strip.text {:abbreviate true :minlength 10}))

;; Figure 7

(-> (base/prop-table late/postdoc :margin 1)
    (lat/dotplot :groups false
                 :index.cond '(function [x y] (median x))
                 :xlab "Proportion" :layout [1 5]
                 :aspect 0.6
                 :scales {:y {:relation "free" :rot 0}}
                 :prepanel (r "function(x, y) {list(ylim = levels(reorder(y, x)))}")
                 :panel '(function [x y ...]
                                   (panel.dotplot x (reorder y x) ...))))

;; Figure 8

(def gcsescore-tab (stats/xtabs (formula "~" gcsescore + gender) chem97))
(def gcsescore-df (let [df (base/as-data-frame gcsescore-tab)]
                    ($<- df 'gcsescore (-> ($ df 'gcsescore)
                                           (base/as-character)
                                           (base/as-numeric)))))

(-> (formula Freq "~" gcsescore | gender)
    (lat/xyplot :data gcsescore-df
                :type "h" :layout [1 2]
                :xlab "Average GCSE Score"))

;; Figure 9

(def score-tab (stats/xtabs (formula "~" score + gender) chem97))
(def score-df (base/as-data-frame score-tab))

(-> (formula Freq "~" score | gender)
    (lat/barchart score-df :origin 0))

;; Chapter 5

;; Figure 1

(-> (r "lat ~ long | cut(depth, 2)")
    (lat/xyplot :data quakes))

;; Figure 2

(-> (r "lat ~ long | cut(depth, 3)")
    (lat/xyplot :data quakes :aspect "iso" :pch "." :cex 2 :type ["p" "g"]
                :xlab "Longitude" :ylab "Latitude"
                :strip (lat/strip-custom :strip.names true :var.name "Depth")))


;; Figure 3

(-> (formula lat "~" long)
    (lat/xyplot :data quakes :aspect "iso"
                :groups '(cut depth :breaks (quantile depth (ppoints 4 1)))
                :auto.key {:columns 3}
                :xlab "Longitude" :ylab "Latitude"))


;; Figure 4

(def depth-col (r/bra (dev/gray-colors 100)
                      (base/cut ($ quakes 'depth) 100 :label false)))

(def depth-ord (base/rev (base/order ($ quakes 'depth))))

(-> (formula lat "~" long)
    (lat/xyplot :data (r/bra quakes depth-ord (r/empty-symbol)) :aspect "iso"
                :type ["p" "g"] :col "black"
                :pch 21 :cex 2 :fill (r/bra depth-col depth-ord)
                :xlab "Longitude" :ylab "Latitude"))

;; Figure 5

(def quakes (-> quakes
                ($<- 'Magnitude (lat/equal-count ($ quakes 'mag) 4))
                ($<- 'color depth-col)))

(base/summary ($ quakes 'Magnitude))

(def quakes-ordered (r/bra quakes depth-ord (r/empty-symbol)))

(-> (formula lat "~" long | Magnitude)
    (lat/xyplot :data quakes-ordered :aspect "iso" :col "black"
                :panel ['function '[x y fill.color ... subscripts]
                        ['<- 'fill [(rsymbol "[") 'fill.color 'subscripts]]
                        '(panel.grid :h -1 :v -1)
                        '(panel.xyplot x y :pch 21 :fill fill ...)]
                :cex 2 :fill.color ($ quakes-ordered 'color)
                :xlab "Longitude" :ylab "Latitude"))


;; Figure 6

(def depth-breaks (lat/do-breaks (base/range ($ quakes-ordered 'depth)) 50))

(def quakes-ordered (-> quakes-ordered
                        ($<- 'color (lat/level-colors ($ quakes-ordered 'depth)
                                                      :at depth-breaks
                                                      :col.regions dev/gray-colors))))
;; nested maps with RObjects do not work yet

#_ (-> (formula lat "~" long | Magnitude)
       (lat/xyplot :data quakes-ordered :aspect "iso"
                   :groups 'color :cex 2 :col "black"
                   :panel ['function '[x y groups ... subscripts]
                           ['<- 'fill [(rsymbol "[") 'groups 'subscripts]]
                           '(panel.grid :h -1 :v -1)
                           '(panel.xyplot x y :pch 21 :fill fill ...)]
                   :legend {:right {:fun lat/draw-colorkey}
                            :args {:key {:col dev/grey-colors
                                         :at depth-breaks}}}
                   :xlab "Longitude" :ylab "Latitude"))

;; Figure 7

(def types-plain ["p", "l", "o", "r", "g", "s", "S", "h", "a", "smooth"])
(def types-horiz ["s", "S", "h", "a", "smooth"])
(def-r horiz (base/rep [false true] [(count types-plain) (count types-horiz)]))
(def-r types (concat types-plain types-horiz))
(base/set-seed 2007041)
(def-r x (r->clj (base/sample (base/seq -10 10 :length 15) 30 true)))
(def n (r->clj (stats/rnorm (count x) :sd 5)))
(def-r y (map (fn [x n]
                (+ x (* 0.25 (inc x) (inc x)) n)) x n))

(r/bra (-> (formula y "~" x | "gl(1,length(types))")
           (lat/xyplot :xlab "type"
                       :ylab (r " list(c('horizontal=TRUE', 'horizontal=FALSE'), y = c(1/6, 4/6))")
                       :as.table true
                       :layout [5 3]
                       :between {:y [0 1]}
                       :strip (r  "function(...) {
                                     panel.fill(trellis.par.get('strip.background')$col[1])
                                     type <- types[panel.number()]
                                     grid.text(lab = sprintf('\"%s\"', type), x = 0.5, y = 0.5)
                                     grid.rect()
                                   }")
                       :scales {:alternating [0 2] :tck [0 0.7] :draw false}
                       :par.settings {:layout.widths {:strip.left [1 0 0 0 0]}}
                       :panel (r "function(...) {
                                    type <- types[panel.number()]
                                    horizontal <- horiz[panel.number()]
                                    panel.xyplot(..., 
                                    type = type,
                                    horizontal = horizontal)
                                  }")))
       (base/rep 1 (count types)))

;; Figure 8

(def earthquake r.MEMSS/Earthquake)

(-> (formula accel "~" distance)
    (lat/xyplot :data earthquake
                :panel '(function [...]
                                  (panel.grid :h -1 :v -1)
                                  (panel.xyplot ...)
                                  (panel.loess ...))
                :xlab "Distance From Epicenter (km)",
                :ylab "Maximum Horizontal Acceleration (g)"))

;; Figure 9

(-> (formula accel "~" distance)
    (lat/xyplot :data earthquake
                :type ["g" "p" "smooth"]
                :scales {:log 2}
                :xlab "Distance From Epicenter (km)",
                :ylab "Maximum Horizontal Acceleration (g)"))

;; Figure 10

(def earthquake ($<- earthquake 'Magnitude
                     (lat/equal-count ($ earthquake 'Richter) 3 :overlap 0.1)))

(def-r coef (-> (formula log2 (accel) "~" log2 (distance))
                (stats/lm :data earthquake)
                (stats/coef)))

(-> (formula accel "~" distance | Magnitude)
    (lat/xyplot :data earthquake
                :scales {:log 2}
                :col.line "grey" :lwd 2
                :panel '(function [...]
                                  (panel.abline :reg coef)
                                  (panel.locfit ...))
                :xlab "Distance From Epicenter (km)",
                :ylab "Maximum Horizontal Acceleration (g)"))

;; Figure 11

(def sweather late/SeatacWeather)

(-> (formula min.temp + max.temp + precip "~" day | month)
    (lat/xyplot :data sweather
                :ylab "Temperature and Rainfall"
                :type "l" :lty 1 :col "black"))

;; Figure 12

(def-r maxp (base/max ($ sweather 'precip) :na.rm true))

(-> (formula min.temp + max.temp + I(80 * precip / maxp) "~" day | month)
    (lat/xyplot :data sweather
                :ylab "Temperature and Rainfall"
                :type ["l", "l", "h"] :lty 1 :col "black"
                :distribute.type true))

;; Figure 13

(stats/update (lat/trellis-last-object)
              :ylab "Temperature (Fahrenheit) \n and Rainfall (inches)"
              :panel (r  "function(...) {
                             panel.xyplot(...)
                             if (panel.number() == 2) {
                                at <- pretty(c(0, maxp))
                                panel.axis('right', half = FALSE,
                                at = at * 80 / maxp, labels = at)
                             }
                           }"))

;; Figure 14

(def gvhd10 late/gvhd10)

(-> (formula asinh (SSC.H) "~" asinh (FL2.H) | Days)
    (lat/xyplot gvhd10 :aspect 1
                :panel r.hexbin/panel-hexbinplot
                :aspect.ratio 1
                :trans base/sqrt))

;; Figure 15

(lat/splom USArrests)

;; Figure 16

(-> (r "~USArrests[c(3, 1, 2, 4)] | state.region")
    (lat/splom :pscales 0 :type ["g" "p" "smooth"]))

;; Figure 17

(-> (r "~data.frame(mpg, disp, hp, drat, wt, qsec)")
    (lat/splom :data mtcars :groups 'cyl :pscales 0
               :varnames ["Miles\nper\ngallon", "Displacement\n(cu. in.)",
                          "Gross\nhorsepower", "Rear\naxle\nratio", 
                          "Weight", "1/4 mile\ntime"]
               :auto.key {:columns 3 :title "Number of Cylinders"}))

;; Figure 18

(-> (r "~mtcars[c(1, 3, 4, 5, 6, 7)] | factor(cyl)")
    (lat/parallel mtcars :groups 'carb
                  :key (-> ($ mtcars 'carb)
                           (base/factor)
                           (base/levels)
                           (lat/simpleKey))
                  :points false :lines true
                  :space "top" :columns 3
                  :layout [3 1]))

;; Figure 19

(-> (r "~ asinh(gvhd10[c(3, 2, 4, 1, 5)])")
    (lat/parallel :data gvhd10
                  :subset '(== Days 13)
                  :alpha 0.01 :lty 1))

(dev/dev-off)

(r/discard-all-sessions)

;; (dev/x11 :width 9)
