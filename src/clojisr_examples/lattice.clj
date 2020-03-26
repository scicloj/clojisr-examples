(ns clojisr-examples.lattice
  "Rework of examples from Lattice book.

  http://lmdvr.r-forge.r-project.org/figures/figures.html"
  (:require [notespace.v2.note :as note
             :refer [note note-void note-md note-as-md note-hiccup note-as-hiccup]]
            [clojisr.v1.r :as r]))

;; Be sure to install following packages:
;;
;; install.packages(c("lattice", "latticeExtra", "mlmrev", "MEMSS", "locfit", "hexbin", "maps", "copula", "logspline"))

(note/defkind note-def :def {:render-src?    true
                             :value-renderer (comp notespace.v2.view/value->hiccup var-get)})

(def target-path (notespace.v2.note/ns->out-dir *ns*))

(note-md "# Lattice")

(note-md "Examples from the Lattice book [page](http://lmdvr.r-forge.r-project.org/figures/figures.html)")

(note-md "Please be sure to install following packages.")

(note-md "```r
install.packages(c('latticeExtra', 'copula', 'ellipse', 'gridBase',
                   'locfit', 'logspline', 'mapproj', 'maps', 'MEMSS',
                   'mlmRev', 'RColorBrewer'))
source('http://bioconductor.org/biocLite.R')
biocLite(c('flowCore', 'flowViz', 'hexbin'))
```")

(note-md :Setup "## Setup")

(note-void (require '[clojisr.v1.r :as r :refer [r+ r r->clj clj->r clj->java java->clj java->r r->java]]
                    '[clojisr.v1.require :refer [require-r]]
                    '[clojisr.v1.applications.plotting :refer [plot->file]]))

(note-void (require-r '[lattice :as lat]
                      '[latticeExtra :as late]
                      '[stats :as stats]
                      '[mlmRev]
                      '[MEMSS]
                      '[MASS]
                      '[grDevices :as dev]
                      '[graphics :as g]
                      '[base :refer [$ $<- <- summary] :as base]
                      '[utils]
                      '[grid]
                      '[locfit]
                      '[hexbin]
                      '[maps]
                      '[mapproj]
                      '[copula]
                      '[logspline]
                      '[RColorBrewer]
                      '[flowViz]
                      '[flowCore]
                      '[datasets :refer :all]))

(note (r->clj r.base/version))

;; Chapter 1

(note-md :Chapter-1 "## Chapter 1")

(note-md "### Figure 1.1")

(note (r->clj (stats/xtabs '(formula nil score) :data r.mlmRev/Chem97)))
;; => [3688 3627 4619 5739 6668 6681]

(note-void (plot->file (str target-path "1.1.png") (-> '(formula nil (| gcsescore (factor score)))
                                                       (lat/histogram :data r.mlmRev/Chem97))))
(note-hiccup [:image {:src "1.1.png"}])

(note-md "### Figure 1.2")

(note-void (plot->file (str target-path "1.2.png") (-> '(formula nil (| gcsescore (factor score)))
                                                       (lat/densityplot :data r.mlmRev/Chem97 :plot.points false :ref true))))
(note-hiccup [:image {:src "1.2.png"}])

(note-md "### Figure 1.3")

(note-void (plot->file (str target-path "1.3.png") (-> '(formula nil gcsescore)
                                                       (lat/densityplot :data r.mlmRev/Chem97 :groups 'score :plot.points false
                                                                        :ref true :auto.key {:columns 3}))))
(note-hiccup [:image {:src "1.3.png"}])

(note-md "### Figure 1.4")

(note-void (def tp1 (-> '(formula nil (| gcsescore (factor score)))
                        (lat/histogram :data r.mlmRev/Chem97))))

(note-void (def tp2 (-> '(formula nil gcsescore)
                        (lat/densityplot :data r.mlmRev/Chem97 :groups 'score :plot.points false
                                         :auto.key {:space "right" :title "score"}))))

(note (base/class tp2))
;; => [1] "trellis"

(note (r->clj (base/summary tp1)))
;; => {:call
;;     [histogram [$ .MEM x481db63caee84aa6] [$ .MEM xad4e82ae81ba44aa]],
;;     :packet.sizes [3688.0 3627.0 4619.0 5739.0 6668.0 6681.0],
;;     :index.cond [[1 2 3 4 5 6]],
;;     :perm.cond [1]}

(note-void (plot->file (str target-path "1.4.png") (fn []
                                                     (g/plot tp1 :split [1 1 1 2])
                                                     (g/plot tp2 :split [1 2 1 2] :newpage false))))
(note-hiccup [:image {:src "1.4.png"}])


(note-md :Chapter-2 "## Chapter 2")

(note-md "### Figure 2.1")

(note-void (def tp1-oats (-> '(formula yield (| nitro (+ Variety Block)))
                             (lat/xyplot :data r.MEMSS/Oats :type "o"))))

(note-void (plot->file (str target-path "2.1.png") #(base/print tp1-oats)))
(note-hiccup [:image {:src "2.1.png"}])

(note-md "### Figure 2.2")

(note (base/dim tp1-oats))
;; => [1] 3 6

(note (r->clj (base/dimnames tp1-oats)))
;; => {:Variety ["Golden Rain" "Marvellous" "Victory"], :Block ["I" "II" "III" "IV" "V" "VI"]}

(note (stats/xtabs '(formula nil (+ Variety Block)) :data r.MEMSS/Oats))
;; =>              Block
;; Variety       I II III IV V VI
;; Golden Rain 4  4   4  4 4  4
;; Marvellous  4  4   4  4 4  4
;; Victory     4  4   4  4 4  4

(note (r->clj (base/summary tp1-oats)))
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

(note (r->clj (base/summary (r/bra tp1-oats nil 1))))
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

(note-void (plot->file (str target-path "2.2.png") (r/bra tp1-oats nil 1)))
(note-hiccup [:image {:src "2.2.png"}])

(note-md "### Figure 2.3")
(note-void (plot->file (str target-path "2.3.png") (stats/update tp1-oats :aspect "xy")))
(note-hiccup [:image {:src "2.3.png"}])

(note-md "### Figure 2.4")
(note-void (plot->file (str target-path "2.4.png") (stats/update tp1-oats :aspect "xy" :layout [0 18])))
(note-hiccup [:image {:src "2.4.png"}])

(note-md "### Figure 2.5")
(note-void (plot->file (str target-path "2.5.png") (stats/update tp1-oats :aspect "xy" :layout [0 18] :between {:x [0 0 0.5] :y 0.5})))
(note-hiccup [:image {:src "2.5.png"}])

;; Figure 6

(note-md "### Figure 2.6")
(note-void (plot->file (str target-path "2.6.png") (-> '(formula variety (| yield site))
                                                       (lat/dotplot lat/barley :layout [1 6] :aspect 0.7 :groups 'year :auto.key {:space "right"}))))
(note-hiccup [:image {:src "2.6.png"}])

;; Figure 7
(note-md "### Figure 2.7")

(note-def
 (def key-variety
   {:space "right"
    :text '[:!list (levels ($ r.MEMSS/Oats Variety))]
    :points {:pch (range 1 3) :col "black"}}))

(note-void (plot->file (str target-path "2.7.png") (-> '(formula yield (| nitro Block))
                                                       (lat/xyplot r.MEMSS/Oats :aspect "xy" :type "o" :groups 'Variety
                                                                   :key key-variety :lty 1 :pch (range 1 3)
                                                                   :col.line "darkgrey" :col.symbol "black"
                                                                   :xlab "Nitrogen concentration (cwt/acre)"
                                                                   :ylab "Yield (bushels/acre)"
                                                                   :main "Yield of three varieties of oats"
                                                                   :sub "A 3 x 4 split plot experiment with 6 blocks"))))
(note-hiccup [:image {:src "2.7.png"}])

;; Figure 8

(note-md "### Figure 2.8")
(note-void (plot->file (str target-path "2.8.png") (-> '(formula Class (| Freq (+ Sex Age)))
                                                       (lat/barchart :data (base/as-data-frame Titanic)
                                                                     :groups 'Survived :stack true :layout [4 1]
                                                                     :auto.key {:title "Survived" :columns 2}))))
(note-hiccup [:image {:src "2.8.png"}])


;; Figure 9

(note-md "### Figure 2.9")
(note-void (plot->file (str target-path "2.9.png") (-> '(formula Class (| Freq (+ Sex Age)))
                                                       (lat/barchart :data (base/as-data-frame Titanic)
                                                                     :groups 'Survived :stack true :layout [4 1]
                                                                     :auto.key {:title "Survived" :columns 2}
                                                                     :scales {:x "free"}))))
(note-hiccup [:image {:src "2.9.png"}])

;; Figure 10
(note-md "### Figure 2.10")

(note-void (def bc-titanic (-> '(formula Class (| Freq (+ Sex Age)))
                               (lat/barchart :data (base/as-data-frame Titanic)
                                             :groups 'Survived :stack true :layout [4 1]
                                             :auto.key {:title "Survived" :columns 2}
                                             :scales {:x "free"}))))

(note-void (plot->file (str target-path "2.10.png")
                       (stats/update bc-titanic :panel '(function [...]
                                                                  (panel.grid :h 0 :v -1)
                                                                  (panel.barchart ...)))))
(note-hiccup [:image {:src "2.10.png"}])

;; Figure 11

(note-md "### Figure 2.11")
(note-void (plot->file (str target-path "2.11.png")
                       (stats/update bc-titanic :panel '(function [... border]
                                                                  (panel.barchart ... :border "transparent")))))
(note-hiccup [:image {:src "2.11.png"}])

(note-md :Chapter-3 "## Chapter 3")

;; Figure 1

(note-md "### Figure 3.1")
(note-void (plot->file (str target-path "3.1.png") (-> '(formula nil eruptions)
                                                       (lat/densityplot :data faithful))))
(note-hiccup [:image {:src "3.1.png"}])

;; Figure 2

(note-md "### Figure 3.2")
(note-void (plot->file (str target-path "3.2.png") (-> '(formula nil eruptions)
                                                       (lat/densityplot :data faithful :kernel "rect" :bw 0.2 :plot.points "rug" :n 200))))
(note-hiccup [:image {:src "3.2.png"}])

;; Figure 3

(note-md "### Figure 3.3")
(note-void (plot->file (str target-path "3.3.png") (-> '(formula nil (| (log FSC.H) Days))
                                                       (lat/densityplot :data late/gvhd10 :plot.points false :ref true :layout [2 4]))))
(note-hiccup [:image {:src "3.3.png"}])

;; Figure 4

(note-md "### Figure 3.4")
(note-void (plot->file (str target-path "3.4.png") (-> '(formula nil (| (log2 FSC.H) Days))
                                                       (lat/histogram :data late/gvhd10 :xlab "log Forward Scatter"
                                                                      :type "density" :nint 50 :layout [2 4]))))
(note-hiccup [:image {:src "3.4.png"}])


;; Figure 5

(note-md "### Figure 3.5")
(note-void (plot->file (str target-path "3.5.png") (-> '(formula nil (| gcsescore (factor score)))
                                                       (lat/qqmath :data r.mlmRev/Chem97 :f.value (stats/ppoints 100)))))
(note-hiccup [:image {:src "3.5.png"}])

;; Figure 6

(note-md "### Figure 3.6")
(note-void (plot->file (str target-path "3.6.png") (-> '(formula nil (| gcsescore gender))
                                                       (lat/qqmath r.mlmRev/Chem97 :groups 'score :aspect "xy" :f.value (stats/ppoints 100)
                                                                   :auto.key {:space "right"}
                                                                   :xlab "Standard Normal Quantiles", 
                                                                   :ylab "Average GCSE Score"))))
(note-hiccup [:image {:src "3.6.png"}])

;; Figure 7
(note-md "### Figure 3.7")

(note-void (def chem97-mod (base/transform r.mlmRev/Chem97 :gcsescore.trans '(** gcsescore 2.34))))

(note-void (plot->file (str target-path "3.7.png") (-> '(formula nil (| gcsescore.trans gender))
                                                       (lat/qqmath chem97-mod :groups 'score :aspect "xy" :f.value (stats/ppoints 100)
                                                                   :auto.key {:space "right" :title "score"}
                                                                   :xlab "Standard Normal Quantiles", 
                                                                   :ylab "Transformed GCSE Score"))))
(note-hiccup [:image {:src "3.7.png"}])

;; Figure 8

(note-md "### Figure 3.8")
(note-void (plot->file (str target-path "3.8.png") (-> '(formula nil (| gcsescore (factor score)))
                                                       (late/ecdfplot :data r.mlmRev/Chem97 :groups 'gender :auto.key {:columns 2}
                                                                      :subset '(> gcsescore 0)
                                                                      :xlab "Average GCSE Score"))))
(note-hiccup [:image {:src "3.8.png"}])

;; Figure 9

(note-md "### Figure 3.9")
(note-void (plot->file (str target-path "3.9.png") (-> '(formula nil (| gcsescore (factor score)))
                                                       (lat/qqmath :data r.mlmRev/Chem97 :groups 'gender :auto.key {:columns 2 :points false :lines true}
                                                                   :type "l" :distribution 'qunif
                                                                   :prepanel 'prepanel.qqmathline :aspect "xy"
                                                                   :subset '(> gcsescore 0)
                                                                   :xlab "Standard Normal Quantiles", 
                                                                   :ylab "Average GCSE Score"))))
(note-hiccup [:image {:src "3.9.png"}])

;; Figure 10

(note-md "### Figure 3.10")
(note-void (plot->file (str target-path "3.10.png") (-> '(formula gender (| gcsescore (factor score)))
                                                        (lat/qq :data r.mlmRev/Chem97 :f.value (stats/ppoints 100) :aspect 1))))
(note-hiccup [:image {:src "3.10.png"}])

;; Figure 11

(note-md "### Figure 3.11")
(note-void (plot->file (str target-path "3.11.png") (-> '(formula (factor score) (| gcsescore gender))
                                                        (lat/bwplot :data r.mlmRev/Chem97 :xlab "Average GCSE Score"))))
(note-hiccup [:image {:src "3.11.png"}])

;; Figure 12

(note-md "### Figure 3.12")
(note-void (plot->file (str target-path "3.12.png") (-> '(formula (** gcsescore 2.34) (| gender  (factor score)))
                                                        (lat/bwplot :data r.mlmRev/Chem97 :varwidth true :layout [6 1]
                                                                    :xlab "Average GCSE Score"))))
(note-hiccup [:image {:src "3.12.png"}])

;; Figure 13

(note-md "### Figure 3.13")
(note-void (plot->file (str target-path "3.13.png") (-> '(formula Days (log FSC.H))
                                                        (lat/bwplot :data late/gvhd10
                                                                    :xlab "log(Forward Scatter)"
                                                                    :ylab "Days Past Transplant"))))
(note-hiccup [:image {:src "3.13.png"}])


;; Figure 14

(note-md "### Figure 3.14")
(note-void (plot->file (str target-path "3.14.png") (-> '(formula Days (log FSC.H))
                                                        (lat/bwplot :data late/gvhd10 :panel 'panel.violin :box.ratio 3
                                                                    :xlab "log(Forward Scatter)"
                                                                    :ylab "Days Past Transplant"))))
(note-hiccup [:image {:src "3.14.png"}])

;; Figure 15

(note-md "### Figure 3.15")
(note-void (plot->file (str target-path "3.15.png") (-> '(formula (factor mag) depth)
                                                        (lat/stripplot quakes))))
(note-hiccup [:image {:src "3.15.png"}])

;; Figure 16

(note-md "### Figure 3.16")
(note-void (plot->file (str target-path "3.16.png") (-> '(formula depth (factor mag))
                                                        (lat/stripplot quakes :jitter.data true :alpha 0.6
                                                                       :xlab "Magnitude (Richter)" :ylab "Depth (km)"))))
(note-hiccup [:image {:src "3.16.png"}])

;; Figure 17

(note-md "### Figure 3.17")
(note-void (plot->file (str target-path "3.17.png") (-> '(formula (sqrt (abs (residuals (lm (formula yield (+ variety year site))))))
                                                                  site)
                                                        (lat/stripplot :data lat/barley :groups 'year :jitter.data true
                                                                       :auto.key {:points true :lines true :columns 2}
                                                                       :type ["p" "a"] :fun 'median
                                                                       :ylab (r "expression(abs('Residual Barley Yield')^{1 / 2})")))))
(note-hiccup [:image {:src "3.17.png"}])

(note-md :Chapter-4 "## Chapter 4")

(note-md "### Figure 4.1")

(note VADeaths)
(note (base/class VADeaths))
(note (r->clj (r.utils/methods "dotplot")))

;; Figure 1
(note-void (plot->file (str target-path "4.1.png")
                       (lat/dotplot VADeaths :groups false)))
(note-hiccup [:image {:src "4.1.png"}])

;; Figure 2

(note-md "### Figure 4.2")
(note-void (plot->file (str target-path "4.2.png")
                       (lat/dotplot VADeaths :groups false
                                    :layout [1 4] :aspect 0.7
                                    :origin 0 :type ["p" "h"]
                                    :main "Death Rates in Virginia - 1940" 
                                    :xlab "Rate (per 1000)")))
(note-hiccup [:image {:src "4.2.png"}])

;; Figure 3

(note-md "### Figure 4.3")
(note-void (plot->file (str target-path "4.3.png")
                       (lat/dotplot VADeaths, :type "o"
                                    :auto.key {:lines true :space "right"}
                                    :main "Death Rates in Virginia - 1940"
                                    :xlab "Rate (per 1000)")))
(note-hiccup [:image {:src "4.3.png"}])

;; Figure 4

(note-md "### Figure 4.4")
(note-void (plot->file (str target-path "4.4.png")
                       (lat/barchart VADeaths :groups false
                                     :layout [1 4] :aspect 0.7
                                     :reference false
                                     :main "Death Rates in Virginia - 1940" 
                                     :xlab "Rate (per 1000)")))
(note-hiccup [:image {:src "4.4.png"}])

;; Figure 5

(note-md "### Figure 4.5")
(note-void (plot->file (str target-path "4.5.png")
                       (-> (base/prop-table late/postdoc :margin 1)
                           (lat/barchart :xlab "Proportion"
                                         :auto.key {:adj 1}))))
(note-hiccup [:image {:src "4.5.png"}])

;; Figure 6

(note-md "### Figure 4.6")
(note-void (plot->file (str target-path "4.6.png")
                       (-> (base/prop-table late/postdoc :margin 1)
                           (lat/dotplot :groups false
                                        :xlab "Proportion"
                                        :par.strip.text {:abbreviate true :minlength 10}))))
(note-hiccup [:image {:src "4.6.png"}])

;; Figure 7

(note-md "### Figure 4.7")
(note-void (plot->file (str target-path "4.7.png")
                       (-> (base/prop-table late/postdoc :margin 1)
                           (lat/dotplot :groups false
                                        :index.cond '(function [x y] (median x))
                                        :xlab "Proportion" :layout [1 5]
                                        :aspect 0.6
                                        :scales {:y {:relation "free" :rot 0}}
                                        :prepanel '(function [x y] {:ylim (levels (reorder y x))})
                                        :panel '(function [x y ...] (panel.dotplot x (reorder y x) ...))))))
(note-hiccup [:image {:src "4.7.png"}])

;; Figure 8

(note-md "### Figure 4.8")

(note-void (def gcsescore-tab (stats/xtabs '(formula nil (+ gcsescore gender)) r.mlmRev/Chem97)))
(note-void (def gcsescore-df (let [df (base/as-data-frame gcsescore-tab)]
                               ($<- df 'gcsescore (-> ($ df 'gcsescore)
                                                      (base/as-character)
                                                      (base/as-numeric))))))

(note-void (plot->file (str target-path "4.8.png")
                       (-> '(formula Freq (| gcsescore gender))
                           (lat/xyplot :data gcsescore-df
                                       :type "h" :layout [1 2]
                                       :xlab "Average GCSE Score"))))
(note-hiccup [:image {:src "4.8.png"}])

;; Figure 9
(note-md "### Figure 4.9")

(note-void (def score-tab (stats/xtabs '(formula nil (+ score gender)) r.mlmRev/Chem97)))
(note-void (def score-df (base/as-data-frame score-tab)))

(note-void (plot->file (str target-path "4.9.png")
                       (-> '(formula Freq (| score gender))
                           (lat/barchart score-df :origin 0))))
(note-hiccup [:image {:src "4.9.png"}])

(note-md :Chapter-5 "## Chapter 5")

;; Figure 1

(note-md "### Figure 5.1")
(note-void (plot->file (str target-path "5.1.png")
                       (-> '(formula lat (| long (cut depth 2)))
                           (lat/xyplot :data quakes))))
(note-hiccup [:image {:src "5.1.png"}])

;; Figure 2

(note-md "### Figure 5.2")
(note-void (plot->file (str target-path "5.2.png")
                       (-> '(formula lat (| long (cut depth 3)))
                           (lat/xyplot :data quakes :aspect "iso" :pch "." :cex 2 :type ["p" "g"]
                                       :xlab "Longitude" :ylab "Latitude"
                                       :strip (lat/strip-custom :strip.names true :var.name "Depth")))))
(note-hiccup [:image {:src "5.2.png"}])


;; Figure 3

(note-md "### Figure 5.3")
(note-void (plot->file (str target-path "5.3.png")
                       (-> '(formula lat long)
                           (lat/xyplot :data quakes :aspect "iso"
                                       :groups '(cut depth :breaks (quantile depth (ppoints 4 1)))
                                       :auto.key {:columns 3}
                                       :xlab "Longitude" :ylab "Latitude"))))
(note-hiccup [:image {:src "5.3.png"}])


;; Figure 4
(note-md "### Figure 5.4")

(note-void (def depth-col (r/bra (dev/gray-colors 100)
                                 (base/cut ($ quakes 'depth) 100 :label false))))
(note-void (def depth-ord (base/rev (base/order ($ quakes 'depth)))))

(note-void (plot->file (str target-path "5.4.png")
                       (-> '(formula lat long)
                           (lat/xyplot :data (r/bra quakes depth-ord nil) :aspect "iso"
                                       :type ["p" "g"] :col "black"
                                       :pch 21 :cex 2 :fill (r/bra depth-col depth-ord)
                                       :xlab "Longitude" :ylab "Latitude"))))
(note-hiccup [:image {:src "5.4.png"}])

;; Figure 
(note-md "### Figure 5.5")

(note-void (def quakes (-> quakes
                           ($<- 'Magnitude (lat/equal-count ($ quakes 'mag) 4))
                           ($<- 'color depth-col))))

(note (base/summary ($ quakes 'Magnitude)))

(note-void (def quakes-ordered (r/bra quakes depth-ord nil)))

(note-void (plot->file (str target-path "5.5.png")
                       (-> '(formula lat (| long Magnitude))
                           (lat/xyplot :data quakes-ordered :aspect "iso" :col "black"
                                       :panel '(function [x y fill.color ... subscripts]
                                                         (<- fill (bra fill.color subscripts))
                                                         (panel.grid :h -1 :v -1)
                                                         (panel.xyplot x y :pch 21 :fill fill ...))
                                       :cex 2 :fill.color ($ quakes-ordered 'color)
                                       :xlab "Longitude" :ylab "Latitude"))))
(note-hiccup [:image {:src "5.5.png"}])

;; Figure 6
(note-md "### Figure 5.6")

(note-void (def depth-breaks (lat/do-breaks (base/range ($ quakes-ordered 'depth)) 50)))

(note-void (def quakes-ordered (-> quakes-ordered
                                   ($<- 'color (lat/level-colors ($ quakes-ordered 'depth)
                                                                 :at depth-breaks
                                                                 :col.regions dev/gray-colors)))))
(note-void (plot->file (str target-path "5.6.png")
                       (-> '(formula lat  (| long Magnitude))
                           (lat/xyplot :data quakes-ordered :aspect "iso"
                                       :groups 'color :cex 2 :col "black"
                                       :panel '(function [x y groups ... subscripts]
                                                         (<- fill (bra groups subscripts))
                                                         (panel.grid :h -1 :v -1)
                                                         (panel.xyplot x y :pch 21 :fill fill ...))
                                       :legend {:right {:fun lat/draw-colorkey
                                                        :args {:key {:col dev/grey-colors
                                                                     :at depth-breaks}
                                                               :draw false}}}
                                       :xlab "Longitude" :ylab "Latitude"))))
(note-hiccup [:image {:src "5.6.png"}])

;; Figure 7
(note-md "### Figure 5.7")

(note-void (def types-plain ["p", "l", "o", "r", "g", "s", "S", "h", "a", "smooth"])
           (def types-horiz ["s", "S", "h", "a", "smooth"])
           (def horiz (base/rep [false true] [(count types-plain) (count types-horiz)]))
           (def types (concat types-plain types-horiz)))

(note-void (base/set-seed 2007041))

(note-void (def x (<- 'x (base/sample (base/seq -10 10 :length 15) 30 true)))
           (def n (stats/rnorm (base/length x) :sd 5))
           (def y (<- 'y (map (fn [x n]
                                (+ x (* 0.25 (inc x) (inc x)) n)) (r->clj x) (r->clj n)))))

(note-void (plot->file (str target-path "5.7.png")
                       (r/bra (-> '(formula y (| x (gl 1 ~(count types))))
                                  (lat/xyplot :xlab "type"
                                              :ylab [:!list ["horizontal=TRUE" "horizontal=FALSE"] :y [1/6 4/6]]
                                              :as.table true
                                              :layout [5 3]
                                              :between {:y [0 1]}
                                              :strip '(function [...]
                                                                (grid.rect)
                                                                (panel.fill (bra ($ (trellis.par.get "strip.background") col) 1))
                                                                (<- type (bra ~types (panel.number)))
                                                                (grid.text :lab (sprintf "%s" type) :x 0.5 :y 0.5))
                                              :scales {:alternating [0 2] :tck [0 0.7] :draw false}
                                              :par.settings {:layout.widths {:strip.left [1 0 0 0 0]}}
                                              :panel '(function [...]
                                                                (<- type (bra ~types (panel.number)))
                                                                (<- horizontal (bra ~horiz (panel.number)))
                                                                (panel.xyplot ... :type type :horizontal horizontal))))
                              (base/rep 1 (base/length types)))))
(note-hiccup [:image {:src "5.7.png"}])

;; Figure 8

(note-md "### Figure 5.8")
(note-void (plot->file (str target-path "5.8.png")
                       (-> '(formula accel distance)
                           (lat/xyplot :data r.MEMSS/Earthquake
                                       :panel '(function [...]
                                                         (panel.grid :h -1 :v -1)
                                                         (panel.xyplot ...)
                                                         (panel.loess ...))
                                       :xlab "Distance From Epicenter (km)",
                                       :ylab "Maximum Horizontal Acceleration (g)"))))
(note-hiccup [:image {:src "5.8.png"}])

;; Figure 9

(note-md "### Figure 5.9")
(note-void (plot->file (str target-path "5.9.png")
                       (-> '(formula accel distance)
                           (lat/xyplot :data r.MEMSS/Earthquake
                                       :type ["g" "p" "smooth"]
                                       :scales {:log 2}
                                       :xlab "Distance From Epicenter (km)",
                                       :ylab "Maximum Horizontal Acceleration (g)"))))
(note-hiccup [:image {:src "5.9.png"}])

;; Figure 10
(note-md "### Figure 5.10")

(note-void (def earthquake ($<- r.MEMSS/Earthquake 'Magnitude
                                (lat/equal-count ($ r.MEMSS/Earthquake 'Richter) 3 :overlap 0.1))))

(note-def (def coef (-> '(formula (log2 accel) (log2 distance))
                        (stats/lm :data earthquake)
                        (stats/coef))))

(note-void (plot->file (str target-path "5.10.png")
                       (-> '(formula accel (| distance Magnitude))
                           (lat/xyplot :data earthquake
                                       :scales {:log 2}
                                       :col.line "grey" :lwd 2
                                       :panel '(function [...]
                                                         (panel.abline :reg ~coef)
                                                         (panel.locfit ...))
                                       :xlab "Distance From Epicenter (km)",
                                       :ylab "Maximum Horizontal Acceleration (g)"))))
(note-hiccup [:image {:src "5.10.png"}])

;; Figure 11

(note-md "### Figure 5.11")
(note-void (plot->file (str target-path "5.11.png")
                       (-> '(formula (+ min.temp max.temp precip) (| day month))
                           (lat/xyplot :data late/SeatacWeather
                                       :ylab "Temperature and Rainfall"
                                       :type "l" :lty 1 :col "black"))))
(note-hiccup [:image {:src "5.11.png"}])

;; Figure 12
(note-md "### Figure 5.12")

(note-def (def maxp (base/max ($ late/SeatacWeather 'precip) :na.rm true)))

(note-void (plot->file (str target-path "5.12.png")
                       (-> '(formula (+ min.temp max.temp (I (* 80  (/ precip ~maxp)))) (| day month))
                           (lat/xyplot :data late/SeatacWeather
                                       :ylab "Temperature and Rainfall"
                                       :type ["l", "l", "h"] :lty 1 :col "black"
                                       :distribute.type true))))
(note-hiccup [:image {:src "5.12.png"}])

;; Figure 13

(note-md "### Figure 5.13")
(note-void (plot->file (str target-path "5.13.png")
                       (stats/update (lat/trellis-last-object)
                                     :ylab "Temperature (Fahrenheit) \n and Rainfall (inches)"
                                     :panel
                                     '(function [...]
                                                (panel.xyplot ...)
                                                (if (== (panel.number) 2)
                                                  (do (<- at (pretty [0 ~maxp]))
                                                      (panel.axis "right" :half false :at (/ (* at 80) ~maxp) :labels at)))))))
(note-hiccup [:image {:src "5.13.png"}])

;; Figure 14

(note-md "### Figure 5.14")
(note-void (plot->file (str target-path "5.14.png")
                       (-> '(formula (asinh SSC.H) (| (asinh FL2.H) Days))
                           (lat/xyplot late/gvhd10 :aspect 1
                                       :panel r.hexbin/panel-hexbinplot
                                       :aspect.ratio 1
                                       :trans base/sqrt))))
(note-hiccup [:image {:src "5.14.png"}])

;; Figure 15

(note-md "### Figure 5.15")
(note-void (plot->file (str target-path "5.15.png")
                       (lat/splom USArrests)))
(note-hiccup [:image {:src "5.15.png"}])

;; Figure 16

(note-md "### Figure 5.16")
(note-void (plot->file (str target-path "5.16.png")
                       (-> '(formula nil (| (bra USArrests [3 1 2 4])
                                            state.region))
                           (lat/splom :pscales 0 :type ["g" "p" "smooth"]))))
(note-hiccup [:image {:src "5.16.png"}])

;; Figure 17

(note-md "### Figure 5.17")
(note-void (plot->file (str target-path "5.17.png")
                       (-> '(formula nil (data.frame mpg disp hp drat wt qsec))
                           (lat/splom :data mtcars :groups 'cyl :pscales 0
                                      :varnames ["Miles\nper\ngallon", "Displacement\n(cu. in.)",
                                                 "Gross\nhorsepower", "Rear\naxle\nratio", 
                                                 "Weight", "1/4 mile\ntime"]
                                      :auto.key {:columns 3 :title "Number of Cylinders"}))))
(note-hiccup [:image {:src "5.17.png"}])

;; Figure 18

(note-md "### Figure 5.18")
(note-void (plot->file (str target-path "5.18.png")
                       (-> '(formula nil (| (bra mtcars [1 3 4 5 6 7])
                                            (factor cyl)))
                           (lat/parallel mtcars :groups 'carb
                                         :key (lat/simpleKey (-> ($ mtcars 'carb)
                                                                 (base/factor)
                                                                 (base/levels))
                                                             :points false :lines true
                                                             :space "top" :columns 3)
                                         :layout [3 1]))))
(note-hiccup [:image {:src "5.18.png"}])

;; Figure 19

(note-md "### Figure 5.19")
(note-void (plot->file (str target-path "5.19.jpg")
                       (-> '(formula nil (asinh (bra gvhd10 [3 2 4 1 5])))
                           (lat/parallel :data late/gvhd10
                                         :subset '(== Days 13)
                                         :alpha 0.01 :lty 1))))
(note-hiccup [:image {:src "5.19.jpg"}])

(note-md :Chapter-6 "## Chapter 6")

(note-md "### Figure 6.1")

(note-void (def quakes ($<- quakes 'Magnitude (lat/equal-count ($ quakes 'mag) 4))))

(note-void (plot->file (str target-path "6.1.png")
                       (-> '(formula depth (| (* lat long) Magnitude))
                           (lat/cloud :data quakes
                                      :zlim (base/rev (base/range ($ quakes 'depth)))
                                      :screen {:z 105 :x -70}
                                      :panel.aspect 0.75
                                      :xlab "Longitude" :ylab "Latitude" :zlab "Depth"))))
(note-hiccup [:image {:src "6.1.png"}])

(note-md "### Figure 6.2")
(note-void (plot->file (str target-path "6.2.png")
                       (-> '(formula depth (| (* lat long) Magnitude))
                           (lat/cloud :data quakes
                                      :zlim (base/rev (base/range ($ quakes 'depth)))
                                      :screen {:z 80 :x -70} :zoom 0.7
                                      :scales {:z {:arrows false :distance 2}}
                                      :panel.aspect 0.75
                                      :xlab "Longitude" :ylab "Latitude"
                                      :zlab [:!list "Depth\n(km)" :rot 90]))))
(note-hiccup [:image {:src "6.2.png"}])

(note-md "### Figure 6.3")

(note-void
 (def p (-> '(formula depth (+ long lat))
            (lat/cloud quakes
                       :zlim [690 30]
                       :pch "." :cex 1.5 :zoom 1
                       :xlab nil :ylab nil :zlab nil
                       :par.settings {:axis.line {:col "transparent"}}
                       :scale {:draw false})))
 (def npanel 4)
 (def rotz (base/seq -30 30 :length npanel))
 (def roty [3 0]))

(note-void (plot->file (str target-path "6.3.png")
                       (stats/update (r/bra p (base/rep 1 (* 2 npanel)))
                                     :layout [2 npanel]
                                     :panel '(function [... screen]
                                                       (<- crow (current.row))
                                                       (<- ccol (current.column))
                                                       (panel.cloud ... :screen {:z (bra ~rotz crow)
                                                                                 :x -60
                                                                                 :y (bra ~roty ccol)})))))
(note-hiccup [:image {:src "6.3.png"}])

(note-md "### Figure 6.4")

(note-void (def state-info (let [state-info (base/data-frame :name state-name
                                                             :long ($ state-center 'x)
                                                             :lat ($ state-center 'y)
                                                             :area (r/bra state-x77 nil "Area")
                                                             :population '(* 1000 (bra state.x77 nil "Population")))]
                             ($<- state-info 'density (base/with state-info '(/ population area))))))

(note-void (plot->file (str target-path "6.4.png")
                       (-> '(formula density (+ long lat))
                           (lat/cloud state-info
                                      :subset '(! (%in% name ["Alaska" "Hawaii"]))
                                      :type "h" :lwd 2 :zlim [0 (base/max ($ state-info 'density))]
                                      :scale {:arrows false}))))
(note-hiccup [:image {:src "6.4.png"}])

(note-md "### Figure 6.5")

(note-void
 (def state-map (r.maps/map "state" :plot false :fill false))
 (def state-map-range-x (r/bra ($ state-map 'range) [1 2]))
 (def state-map-range-y (r/bra ($ state-map 'range) [3 4]))
 (def panel-3dmap (r '(function [... rot.mat distance xlim ylim zlim xlim.scaled ylim.scaled zlim.scaled]
                                (<- scaled.val (function [x original scaled]
                                                         (+ (bra scaled 1)
                                                            (/ (* (- x (bra original 1))
                                                                  (diff scaled))
                                                               (diff original)))))
                                (<- m (ltransform3dto3d (rbind (scaled.val ($ ~state-map x) xlim xlim.scaled)
                                                               (scaled.val ($ ~state-map y) ylim ylim.scaled)
                                                               (bra zlim.scaled 1))
                                                        rot.mat distance))
                                (panel.lines (bra m 1 nil)
                                             (bra m 2 nil)
                                             :col "grey76")))))

(note-void (plot->file (str target-path "6.5.png")
                       (-> '(formula density (+ long lat))
                           (lat/cloud state-info
                                      :subset '(! (%in% name ["Alaska" "Hawaii"]))
                                      :panel.3d.cloud '(function [...]
                                                                 (~panel-3dmap ...)
                                                                 (panel.3dscatter ...))
                                      :type "h" :scales {:draw false} :zoom 1.1
                                      :xlim state-map-range-x
                                      :ylim state-map-range-y
                                      :xlab nil :ylab nil :zlab nil
                                      :aspect '[(/ (diff ~state-map-range-y)
                                                   (diff ~state-map-range-x)) 0.3]
                                      :panel.aspect 0.75 :lwd 2 :screen {:z 30 :x -60}
                                      :par.settings {:axis.line {:col "transparent"}
                                                     :box.3d {:col "transparent" :alpha 0}}))))
(note-hiccup [:image {:src "6.5.png"}])

(note-md "### Figure 6.6")

(note (def env (-> lat/environmental
                   ($<- 'ozone (r/r** ($ lat/environmental 'ozone) 1/3))
                   ($<- 'Radiation (lat/equal-count ($ lat/environmental 'radiation) 4)))))

(note-void (plot->file (str target-path "6.6.png")
                       (lat/cloud '(formula ozone (| (+ wind temperature) Radiation)) env)))
(note-hiccup [:image {:src "6.6.png"}])

(note-md "### Figure 6.7")
(note-void (plot->file (str target-path "6.7.png")
                       (lat/splom (r/bra env [1 2 3 4]))))
(note-hiccup [:image {:src "6.7.png"}])

(note-md "### Figure 6.8")

(note-def
 (def fm1-env (stats/lm '(formula ozone (* radiation temperature wind)) env)))
(note-def
 (def fm2-env (stats/loess '(formula ozone (* wind temperature radiation)) env
                           :span 0.75 :degree 1)))
(note-def
 (def fm3-env (stats/loess '(formula ozone (* wind temperature radiation)) env
                           :parametric ["radiation" "wind"]
                           :span 0.75 :degree 2)))
(note-def
 (def fm4-env (r.locfit/locfit '(formula ozone (* wind temperature radiation)) env)))

(note (def w-mesh (r '(with ~env (do.breaks (range wind) 50))))
      (def t-mesh (r '(with ~env (do.breaks (range temperature) 50))))
      (def r-mesh (r '(with ~env (do.breaks (range radiation) 3))))
      (def grid (as-> (base/expand-grid :wind w-mesh :temperature t-mesh :radiation r-mesh) grid
                  (r/brabra<- grid "fit.linear" :value (stats/predict fm1-env :newdata grid))
                  (r/brabra<- grid "fit.loess.1" :value (base/as-vector (stats/predict fm2-env :newdata grid)))
                  (r/brabra<- grid "fit.loess.2" :value (base/as-vector (stats/predict fm3-env :newdata grid)))
                  (r/brabra<- grid "fit.locfit" :value (stats/predict fm4-env :newdata grid)))))

(note-void (plot->file (str target-path "6.8.png")
                       (-> '(formula (+ fit.linear fit.loess.1 fit.loess.2 fit.locfit)
                                     (| (* wind temperature) radiation))
                           (lat/wireframe grid :outer true :shade true :zlab ""))))
(note-hiccup [:image {:src "6.8.png"}])


(note-md "### Figure 6.9")
(note-void (plot->file (str target-path "6.9.png")
                       (-> '(formula (+ fit.linear fit.loess.1 fit.loess.2 fit.locfit)
                                     (| (* wind temperature) radiation))
                           (lat/levelplot :data grid))))
(note-hiccup [:image {:src "6.9.png"}])

(note-md "### Figure 6.10")
(note-void (plot->file (str target-path "6.10.png")
                       (-> '(formula fit.locfit (| (* wind temperature) radiation))
                           (lat/contourplot grid :aspect 0.7 :layout [1 4] :cuts 15 :label.style "align"))))
(note-hiccup [:image {:src "6.10.png"}])

(note-md "### Figure 6.11")
(note-void (plot->file (str target-path "6.11a.png")
                       (lat/levelplot volcano)))
(note-void (plot->file (str target-path "6.11b.png")
                       (lat/contourplot volcano :cuts 20 :label false)))
(note-void (plot->file (str target-path "6.11c.png")
                       (lat/wireframe volcano :panel.aspect 0.7 :zoom 1)))
(note-hiccup [:div
              [:image {:src "6.11a.png"}]
              [:image {:src "6.11b.png"}]
              [:image {:src "6.11c.png"}]])

(note-md "### Figure 6.12")

(note-void
 (def cor-cars93 (stats/cor (r/bra r.MASS/Cars93
                                   nil
                                   (r/r! (base/sapply r.MASS/Cars93 base/is-factor)))
                            :use "pair")))

(note-void (plot->file (str target-path "6.12.png")
                       (-> cor-cars93
                           (lat/levelplot :scales {:x {:rot 90}}))))
(note-hiccup [:image {:src "6.12.png"}])

(note-md "### Figure 6.13")

(note-def
 (def ord (-> cor-cars93
              (stats/dist)
              (stats/hclust)
              (stats/as-dendrogram)
              (stats/order-dendrogram))))

(note-void (plot->file (str target-path "6.13.png")
                       (-> (r/bra cor-cars93 ord ord)
                           (lat/levelplot :at (lat/do-breaks [-1.01 1.01] 20)
                                          :scales {:x {:rot 90}}))))
(note-hiccup [:image {:src "6.13.png"}])

(note-md "### Figure 6.14")

(note (r/data 'Chem97 "mlmRev"))

(note-void
 (def chem97 ($<- r.mlmRev/Chem97 'gcd
                  '(with Chem97
                         (cut gcsescore :breaks (quantile gcsescore (ppoints 11 :a 1))))))
 (def chem-tab (stats/xtabs '(formula nil (+ score gcd gender)) chem97))
 (def chem-tab-df (base/as-data-frame-table chem-tab))
 (def tick-at (-> chem-tab-df
                  ($ 'Freq)
                  (base/sqrt)
                  (base/range)
                  (base/pretty))))

(note-void (plot->file (str target-path "6.14.png")
                       (-> '(formula (sqrt Freq) (| (* score gcd) gender))
                           (lat/levelplot chem-tab-df
                                          :shrink [0.7 1]
                                          :colorkey {:labels {:at tick-at :labels (r/r** tick-at 2)}}
                                          :aspect "iso"))))
(note-hiccup [:image {:src "6.14.png"}])

(note-md "### Figure 6.15")
(note-void (plot->file (str target-path "6.15.png")
                       (-> '(formula Freq (| (* score gcd) gender))
                           (lat/cloud :data chem-tab-df
                                      :screen {:z -40 :x -25} :zoom 1.1
                                      :col.facet "grey" :xbase 0.7 :ybase 0.6
                                      :par.settings {:box.3d {:col "transparent"}}
                                      :aspect [1.5 0.75] :panel.aspect 0.75
                                      :panel.3d.cloud late/panel-3dbars))))
(note-hiccup [:image {:src "6.15.png"}])
(note-md "### Figure 6.16")

(note-void
 (def grid (let [grid (base/expand-grid :u (lat/do-breaks [0.01 0.99] 25)
                                        :v (lat/do-breaks [0.01 0.99] 25))
                 cb (base/cbind ($ grid 'u) ($ grid 'v))]
             (-> grid
                 ($<- 'frank (r.copula/dCopula cb (r.copula/frankCopula 2)))
                 ($<- 'gumbel (r.copula/dCopula cb (r.copula/gumbelCopula 1.2)))
                 ($<- 'normal (r.copula/dCopula cb (r.copula/normalCopula 0.4)))
                 ($<- 't (r.copula/dCopula cb (r.copula/tCopula 0.4)))))))

(note-void (plot->file (str target-path "6.16.png")
                       (-> '(formula (+ frank gumbel normal t)
                                     (* u v))
                           (lat/wireframe grid :outer true :zlab ""
                                          :screen {:z -30 :x -50}))))
(note-hiccup [:image {:src "6.16.png"}])


(note-md "### Figure 6.17")
(note-void (plot->file (str target-path "6.17.png")
                       (-> '(formula (+ frank gumbel normal t)
                                     (* u v))
                           (lat/wireframe grid :outer true :zlab ""
                                          :screen {:z -30 :x -50}
                                          :scales {:z {:log true}}))))
(note-hiccup [:image {:src "6.17.png"}])

(note-md "### Figure 6.18")

(note-void
 (def rr 2)
 (def tt 1)
 (def n 50)
 (def kx (<- 'kx '(function [u v] (* (cos u)
                                     (- (+ ~rr (* (cos (/ u 2))
                                                  (sin (* ~tt v))))
                                        (* (sin (/ u 2))
                                           (sin (* 2 ~tt v))))))))
 (def ky (<- 'ky '(function [u v] (* (sin u)
                                     (- (+ ~rr (* (cos (/ u 2))
                                                  (sin (* ~tt v))))
                                        (* (sin (/ u 2))
                                           (sin (* 2 ~tt v))))))))
 (def kz (<- 'kz '(function [u v] (+ (* (sin (/ u 2))
                                        (sin (* ~tt v)))
                                     (* (cos (/ u 2))
                                        (sin (* ~tt v)))))))
 (def u (r/r* (base/seq 0.3 1.25 :length n) 2 base/pi))
 (def v (r/r* (base/seq 0 1 :length n) 2 base/pi))
 (def um (<- 'um (base/matrix u (base/length u) (base/length v))))
 (def vm (<- 'vm (base/matrix v (base/length v) (base/length u) :byrow true))))

(note-void (plot->file (str target-path "6.18.png")
                       (-> '(formula (kz um vm)
                                     (+ (kx um vm)
                                        (ky um vm)))
                           (lat/wireframe :shade true
                                          :screen {:z 170 :x -60}
                                          :alpha 0.75 :panel.aspect 0.6 :aspect [1 0.4]))))
(note-hiccup [:image {:src "6.18.png"}])

(note-md "### Figure 6.19")

(note (r.utils/str late/USAge-df))

(note-void
 (def brewer-div (dev/colorRampPalette (r.RColorBrewer/brewer-pal 11 "Spectral") :interpolate "spline")))

(note-void (plot->file (str target-path "6.19.png")
                       (-> '(formula Population (| (* Year Age) Sex))
                           (lat/levelplot :data late/USAge-df
                                          :cuts 199 :col.regions (brewer-div 200)
                                          :aspect "iso"))))
(note-hiccup [:image {:src "6.19.png"}])

(note-md :Chapter-7 "## Chapter 7")

(note-md "### Figure 7.1")

(note-void
 (def vad-plot (-> '(formula (reorder Var2 Freq) (| Freq Var1))
                   (lat/dotplot :data (base/as-data-frame-table VADeaths)
                                :origin 0 :type ["p" "h"]
                                :main "Death Rates in Virgninia - 1940"
                                :xlab "Number of deaths per 100"))))

(note-void (plot->file (str target-path "7.1.png")
                       vad-plot))
(note-hiccup [:image {:src "7.1.png"}])

(note-md "### Figure 7.2")

(note-void (def dot-line-settings (lat/trellis-par-get "dot.line")))
(note (r->clj dot-line-settings))
(note-void (def dot-line-settings ($<- dot-line-settings 'col "transparent")))
(note-void (lat/trellis-par-set "dot.line" dot-line-settings))

(note-void (def plot-line-settings (lat/trellis-par-get "plot.line")))
(note (r->clj plot-line-settings))
(note-void (def plot-line-settings ($<- plot-line-settings 'lwd 2)))
(note-void (lat/trellis-par-set "plot.line" plot-line-settings))

(note-void (plot->file (str target-path "7.2.png")
                       vad-plot))
(note-hiccup [:image {:src "7.2.png"}])

(note-md "### Figure 7.2 (alternative)")

(note-void
 (def panel-dotline (<- 'panel.dotline '(function [x y
                                                   :col ($ dot.symbol col)
                                                   :pch ($ dot.symbol pch)
                                                   :cex ($ dot.symbol cex)
                                                   :alpha ($ dot.symbol alpha)
                                                   :col.line ($ plot.line col)
                                                   :lty ($ plot.line lty)
                                                   :lwd ($ plot.line lwd)
                                                   :alpha.line ($ plot.line alpha)
                                                   ...]
                                                  (<- dot.symbol (trellis.par.get "dot.symbol"))
                                                  (<- plot.line (trellis.par.get "plot.line"))
                                                  (panel.segments 0 y x y :col col.line :lty lty
                                                                  :lwd lwd :alpha alpha.line)
                                                  (panel.points x y :col col :pch pch :cex cex :alpha alpha)))))

(note-void (lat/trellis-par-set :dot.line dot-line-settings :plot.line plot-line-settings))
(note-void (lat/trellis-par-set :dot.line {:col "transparent"} :plot.line {:lwd 2}))
(note-void (lat/trellis-par-set {:dot.line {:col "transparent"}
                                 :plot.line {:lwd 2}}))

(note-void (plot->file (str target-path "7.2a.png")
                       (stats/update vad-plot
                                     :par.settings {:dot.line {:col "transparent"}
                                                    :plot.line {:lwd 2}})))
(note-hiccup [:image {:src "7.2a.png"}])

(note-md "### Figure 7.3")

(note-void
 (def unusual ["grid.pars" "fontsize" "clip" "axis.components" "layout.heights" "layout.widths"])
 (def tp (reduce #(r/brabra<- %1 %2 :value nil) (lat/trellis-par-get) unusual))
 (def names-tp (base/lapply tp base/names))
 (def unames (-> names-tp base/unlist base/unique base/sort))
 (def ans (let [tans (-> (base/matrix 0 :nrow (base/length names-tp) :ncol (base/length unames))
                         (base/rownames<- (base/names names-tp))
                         (base/colnames<- unames))]
            (as-> (reduce
                   #(r/bra<- %1 %2 nil (base/as-numeric (base/%in% unames (r/brabra names-tp %2))))
                   tans (r->clj (base/seq :along names-tp))) ans
              (r/bra ans nil `(order (- (colSums ~ans))))
              (r/bra ans (-> ans base/rowSums base/order) nil)
              (r/bra<- ans `(== ~ans 0) 'NA)))))

(note-void (plot->file (str target-path "7.3.png")
                       (lat/levelplot (base/t ans)
                                      :colorkey false
                                      :scales {:x {:rot 90}}
                                      :panel '(function [x y z ...]
                                                        (panel.abline :v (unique (as.numeric x))
                                                                      :h (unique (as.numeric y))
                                                                      :col "darkgrey")
                                                        (panel.xyplot x y :pch (* 16 z) ...))
                                      :xlab "Graphical parameters" 
                                      :ylab "Setting names")))
(note-hiccup [:image {:src "7.3.png"}])

(note-md "### Figure 7.4")
(note-void (plot->file (str target-path "7.4.png")
                       (lat/show-settings)))
(note-hiccup [:image {:src "7.4.png"}])

(note-md :Chapter-8 "## Chapter 8")

(note-md "### Figure 8.1")
(note-void (plot->file (str target-path "8.1.png")
                       (-> '(formula depth (factor mag))
                           (lat/stripplot :data quakes
                                          :jitter.data true
                                          :scales {:y "free" :rot 0}
                                          :prepanel '(function [x y ...] {:ylim (rev (range y))})
                                          :xlab "Magnitude (Richter scale)"))))
(note-hiccup [:image {:src "8.1.png"}])

(note-md "### Figure 8.2")
(note-void (plot->file (str target-path "8.2.png")
                       (-> '(formula (/ counts 1000)
                                     (| time (equal.count (as.numeric time) 9 :overlap 0.1)))
                           (lat/xyplot late/biocAccess :type "l" :aspect "xy" :strip false
                                       :ylab "Numer of accesses (thousands)" :xlab ""
                                       :scales {:x {:relation "sliced" :axs "i"}
                                                :y {:alternating false}}))))
(note-hiccup [:image {:src "8.2.png"}])

(note-md "### Figure 8.3")
(note-void (plot->file (str target-path "8.3.png")
                       (-> '(formula accel distance)
                           (lat/xyplot :data r.MEMSS/Earthquake
                                       :prepanel lat/prepanel-loess
                                       :aspect "xy"
                                       :type ["p" "g" "smooth"]
                                       :scales {:log 2}
                                       :xlab "Distance From Epicenter (km)"
                                       :ylab "Maximum Horizontal Acceleration (g)"))))
(note-hiccup [:image {:src "8.3.png"}])

(note-md "### Figure 8.4")

(note-void
 (def yscale-components-log2 (r '(function [...]
                                           (<- ans (yscale.components.default ...))
                                           (<- ($ ans right) ($ ans left))
                                           (<- ($ ans left labels labels) (parse :text ($ ans left labels labels)))
                                           (<- ($ ans right labels labels) (~r.MASS/fractions (** 2 ($ ans right labels at))))
                                           ans)))

 (def log-ticks (r '(function [lim :loc [1 5]]
                              (<- ii (+ (floor (log10 (range lim))) [-1 2]))
                              (<- main (** 10 (colon (bra ii 1) (bra ii 2))))
                              (<- r (as.numeric (outer loc main "*")))
                              (bra r (& (<= (bra lim 1) r)
                                        (<= r (bra lim 2)))))))

 (def xscale-components-log2 (r '(function [lim ...]
                                           (<- ans (xscale.components.default :lim lim ...))
                                           (<- tick.at (~log-ticks (** 2 lim) :loc [1 3]))
                                           (<- ($ ans bottom ticks at) (log tick.at 2))
                                           (<- ($ ans bottom labels at) (log tick.at 2))
                                           (<- ($ ans bottom labels labels) (as.character tick.at))
                                           ans))))

(note-void (plot->file (str target-path "8.4.png")
                       (-> '(formula accel (| distance (cut Richter [4.9 5.5 6.5 7.8])))
                           (lat/xyplot :data r.MEMSS/Earthquake
                                       :type ["p" "g"]
                                       :scales {:log 2 :y {:alternating 3}}
                                       :xlab "Distance From Epicenter (km)"
                                       :ylab "Maximum Horizontal Acceleration (g)"
                                       :xscale.components xscale-components-log2
                                       :yscale.components yscale-components-log2))))
(note-hiccup [:image {:src "8.4.png"}])

(note-md "### Figure 8.5")

(note-void
 (def xscale-components-log10 (r '(function [lim ...]
                                            (<- ans (xscale.components.default :lim lim ...))
                                            (<- tick.at (~log-ticks (** 10 lim) :loc (colon 1 9)))
                                            (<- tick.at.major (~log-ticks (** 10 lim) :loc 1))
                                            (<- major (%in% tick.at tick.at.major))
                                            (<- ($ ans bottom ticks at) (log tick.at 10))
                                            (<- ($ ans bottom ticks tck) (ifelse major 1.5 0.75))
                                            (<- ($ ans bottom labels at) (log tick.at 10))
                                            (<- ($ ans bottom labels labels) (as.character tick.at))
                                            (<- (bra ($ ans bottom labels labels) (! major)) "")
                                            (<- ($ ans bottom labels check.overlap) false)
                                            ans))))

(note-void (plot->file (str target-path "8.5.png")
                       (-> '(formula accel distance)
                           (lat/xyplot :data r.MEMSS/Earthquake
                                       :prepanel lat/prepanel-loess
                                       :aspect "xy"
                                       :type ["p" "g"]
                                       :scales {:log 10}
                                       :xlab "Distance From Epicenter (km)"
                                       :ylab "Maximum Horizontal Acceleration (g)"
                                       :xscale.components xscale-components-log10))))
(note-hiccup [:image {:src "8.5.png"}])

(note-md "### Figure 8.6")

(note-void
 (def axis-cf (r '(function [side ...]
                            (if (== side "right")
                              (do
                                (<- F2C (function [f] (/ (* 5 (- f 32)) 9)))
                                (<- C2F (function [c] (+ 32 (/ (* 9 c) 5))))
                                (<- ylim ($ (current.panel.limits) ylim))
                                (<- prettyF (pretty ylim))
                                (<- prettyC (pretty (F2C ylim)))
                                (panel.axis :side side :outside true :at prettyF :tck 5 :line.col "grey65" :text.col "grey35")
                                (panel.axis :side side :outside true :at (C2F prettyC) :labels (as.character prettyC)
                                            :tck 1 :line.col "black" :text.col "black"))
                              (axis.default :side side ...))))))

(note-void (plot->file (str target-path "8.6.png")
                       (-> '(formula nhtemp (time nhtemp))
                           (lat/xyplot :aspect "xy" :type "o"
                                       :scales {:y {:alternating 2 :tck [1 5]}}
                                       :xlab "Year" :ylab "Temperature"
                                       :main "Yearly temperature in New Haven, CT"
                                       :key {:text [:!list ["(Celcius)" "(Fahrenheit)"] :col ["black" "grey35"]] :columns 2}
                                       :axis axis-cf))))
(note-hiccup [:image {:src "8.6.png"}])

(note-md :Chapter-9 "## Chapter 9")

(note-md "### Figure 9.1 ")

(note (base/table ($ r.MASS/Cars93 'Cylinders)))
(note-def
 (def sup-sym (lat/Rows (lat/trellis-par-get "superpose.symbol") (r/colon 1 5))))

(note-void (plot->file (str target-path "9.1a.png")
                       (-> '(formula Price (| EngineSize (reorder AirBags Price)))
                           (lat/xyplot r.MASS/Cars93
                                       :groups 'Cylinders :subset '(!= Cylinders "rotary")
                                       :scales {:y {:log 2 :tick.number 3}}
                                       :xlab "Engine Size (litres)"
                                       :ylab "Average Price (1000 USD)"
                                       :key {:text [:!list (r/bra (base/levels ($ r.MASS/Cars93 'Cylinders)) [1 2 3 4 5])]
                                             :points sup-sym :space "right"}))))
(note-hiccup [:image {:src "9.1a.png"}])

(note-md "### Figure 9.1 (alternative, using auto.key)")
(note-void (plot->file (str target-path "9.1b.png")
                       (-> '(formula Price (| EngineSize (reorder AirBags Price)))
                           (lat/xyplot r.MASS/Cars93
                                       :groups 'Cylinders :subset '(!= Cylinders "rotary")
                                       :scales {:y {:log 2 :tick.number 3}}
                                       :xlab "Engine Size (litres)"
                                       :ylab "Average Price (1000 USD)"
                                       :auto.key {:text (r/bra (base/levels ($ r.MASS/Cars93 'Cylinders)) [1 2 3 4 5])
                                                  :points true :space "right"}))))
(note-hiccup [:image {:src "9.1b.png"}])

(note-md "### Figure 9.1 (yet another alternative, using drop=TRUE)")
(note-void (plot->file (str target-path "9.1c.png")
                       (-> '(formula Price (| EngineSize (reorder AirBags Price)))
                           (lat/xyplot :data (base/subset r.MASS/Cars93 '(!= Cylinders "rotary"))
                                       :groups '(bra Cylinders nil (= drop true))
                                       :scales {:y {:log 2 :tick.number 3}}
                                       :xlab "Engine Size (litres)"
                                       :ylab "Average Price (1000 USD)"
                                       :auto.key {:space "right"}))))
(note-hiccup [:image {:src "9.1c.png"}])

(note-md "### Figure 9.2")

(def my-pch [21 22 23 24 25 20])
(def my-fill ["transparent" "grey" "black"])

(note-md "We can't use `with` here. `with` creates context before function is called which is not possible in Clojure.")

(note-void (plot->file (str target-path "9.2.png")
                       (-> '(formula ($ ~r.MASS/Cars93 Price) ($ ~r.MASS/Cars93 EngineSize))
                           (lat/xyplot :scales {:y {:log 2 :tick.number 3}}
                                       :panel '(function [x y ... subscripts]
                                                         (<- pch (bra ~my-pch (bra ($ ~r.MASS/Cars93 Cylinders) subscripts)))
                                                         (<- fill (bra ~my-fill (bra ($ ~r.MASS/Cars93 AirBags) subscripts)))
                                                         (panel.xyplot x y :pch pch :fill fill :col "black"))
                                       :key [{:space "right" :adj 1}
                                             {:text [:!list (base/levels ($ r.MASS/Cars93 'Cylinders))]
                                              :points {:pch my-pch}}
                                             {:text [:!list (base/levels ($ r.MASS/Cars93 'AirBags))]
                                              :points {:pch 21 :fill my-fill}}
                                             {:rep false}]))))
(note-hiccup [:image {:src "9.2.png"}])

(note-md "### Figure 9.3")

(note-def
 (def hc1 (-> USArrests
              (stats/dist :method "canberra")
              (stats/hclust)
              (stats/as-dendrogram))))
(note-def
 (def ord-hc1 (stats/order-dendrogram hc1)))
(note-def
 (def hc2 (stats/reorder hc1 (r/bra state-region ord-hc1))))
(note-def
 (def ord-hc2 (stats/order-dendrogram hc2)))
(note-def
 (def region-colors ($ (lat/trellis-par-get "superpose.polygon") 'col)))

(note-void (plot->file (str target-path "9.3.png")
                       (lat/levelplot (r/bra (base/t (base/scale USArrests))
                                             nil ord-hc2)
                                      :scales {:x {:rot 90}}
                                      :colorkey false
                                      :legend {:right {:fun late/dendrogramGrob
                                                       :args {:x hc2 :ord ord-hc2
                                                              :side "right" :size 10 :size.add 0.5
                                                              :add {:rect {:col "transparent"
                                                                           :fill (r/bra region-colors state-region)}}
                                                              :type "rectangle"}}})))
(note-hiccup [:image {:src "9.3.png"}])

(note-md :Chapter-10 "## Chapter 10")

(note-md "### Figure 10.1")

(note-def
 (def Titanic1 (-> Titanic
                   (r/bra nil nil "Adult" nil)
                   (base/as-table)
                   (base/as-data-frame))))

(note-void (plot->file (str target-path "10.1.png")
                       (-> '(formula Class (| Freq Sex))
                           (lat/barchart Titanic1
                                         :groups 'Survived :stack true
                                         :auto.key {:title "Survived" :columns 2}))))
(note-hiccup [:image {:src "10.1.png"}])

(note-md "### Figure 10.2")

(note-def
 (def Titanic2 (-> (stats/reshape Titanic1 :direction "wide" :v.names "Freq"
                                  :idvar ["Class" "Sex"] :timevar "Survived")
                   (base/names<- ["Class" "Sex" "Dead" "Alive"]))))

(note-void (plot->file (str target-path "10.2.png")
                       (-> '(formula Class (| (+ Dead Alive) Sex))
                           (lat/barchart Titanic2 :stack true
                                         :auto.key {:columns 2}))))
(note-hiccup [:image {:src "10.2.png"}])

(note-md "### Figure 10.3")
(note-void (plot->file (str target-path "10.3.png")
                       (-> '(formula written (| course gender))
                           (lat/xyplot :data r.mlmRev/Gcsemv
                                       :type ["g" "p" "smooth"]
                                       :xlab "Coursework score" :ylab "Written exam score"
                                       :panel '(function [x y ...]
                                                         (panel.xyplot x y ...)
                                                         (panel.rug :x (bra x (is.na y))
                                                                    :y (bra y (is.na x))))))))
(note-hiccup [:image {:src "10.3.png"}])

(note-md "### Figure 10.4")
(note-void (plot->file (str target-path "10.4.png")
                       (-> '(formula nil (+ written course))
                           (lat/qqmath r.mlmRev/Gcsemv
                                       :type ["p" "g"]
                                       :outer true
                                       :groups 'gender
                                       :auto.key {:columns 2}))))
(note-hiccup [:image {:src "10.4.png"}])

(note-md "### Figure 10.5")

(note-void (base/set-seed 200510128)
           (def x1 (let [x1 (stats/rexp 2000)]
                     (r/bra x1 `(> ~x1 1))))
           (def x2 (stats/rexp 1000)))

(note-void (plot->file (str target-path "10.5.png")
                       (-> '(formula nil data)
                           (lat/qqmath (lat/make-groups :x1 x1 :x2 x2)
                                       :groups 'which
                                       :distribution stats/qexp :aspect "iso"
                                       :type ["p" "g"]
                                       :xlab "exp"))))
(note-hiccup [:image {:src "10.5.png"}])

(note-md "### Figure 10.6")

(note-void
 (def beavers (let [beavers (lat/make-groups :beaver1 beaver1 :beaver2 beaver2)]
                ($<- beavers 'hour
                     (base/with beavers '(+ ("`%/%`" time 100)
                                            (* 24 (- day 307))
                                            (/ (%% time 100) 60)))))))

(note-void (plot->file (str target-path "10.6.png")
                       (-> '(formula temp (| hour which))
                           (lat/xyplot :data beavers
                                       :groups 'activ
                                       :auto.key {:text ["inactive" "active"]
                                                  :columns 2}
                                       :xlab "Time (hours)" :ylab "Body Temperature (C)" 
                                       :scale {:x {:relation "sliced"}}))))
(note-hiccup [:image {:src "10.6.png"}])


(note (r.utils/head late/USAge-df))

(note-md "### Figure 10.7")
(note-void (plot->file (str target-path "10.7.png")
                       (-> '(formula Population (| Age (factor Year)))
                           (lat/xyplot late/USAge-df
                                       :groups 'Sex :type ["l" "g"]
                                       :auto.key {:points false :lines true :columns 2}
                                       :aspect "xy" :ylab "Population (millions)"
                                       :subset '(%in% Year (seq 1905 1975 :by 10))))))
(note-hiccup [:image {:src "10.7.png"}])

(note-md "### Figure 10.8")
(note-void (plot->file (str target-path "10.8.png")
                       (-> '(formula Population (| Year (factor Age)))
                           (lat/xyplot late/USAge-df
                                       :groups 'Sex :type "l"
                                       :strip false :strip.left true
                                       :layout [1 3] :ylab "Population (millions)"
                                       :auto.key {:points false :lines true :columns 2}
                                       :subset '(%in% Age [0 10 20])))))
(note-hiccup [:image {:src "10.8.png"}])

(note-md "### Figure 10.9")
(note-void (plot->file (str target-path "10.9.png")
                       (-> '(formula Population (| Year (factor (- Year Age))))
                           (lat/xyplot late/USAge-df
                                       :groups 'Sex :subset '(%in% (- Year Age) (colon 1894 1905))
                                       :type ["g" "l"] :ylab "Population (millions)"
                                       :auto.key {:points false :lines true :columns 2}))))
(note-hiccup [:image {:src "10.9.png"}])

(note-md "### Figure 10.10")
(note-void (plot->file (str target-path "10.10.png")
                       (-> '(formula stations mag)
                           (lat/xyplot quakes :jitter.x true
                                       :type ["p" "smooth"]
                                       :xlab "Magnitude (Richter)"
                                       :ylab "Number of stations reporting"))))
(note-hiccup [:image {:src "10.10.png"}])


(note-md "### Figure 10.11")

(note-void (def quakes ($<- quakes 'Mag (lat/equal-count ($ quakes 'mag) :number 10 :overlap 0.2)))

           (def ps-mag (g/plot ($ quakes 'Mag)
                               :ylab "Level",
                               :xlab "Magnitude (Richter)"))
           (def bwp-quakes (lat/bwplot '(formula stations Mag) quakes
                                       :xlab "Magnitude", 
                                       :ylab "Number of stations reporting")))

(note-void (plot->file (str target-path "10.11.png")
                       (fn []
                         (g/plot bwp-quakes :position [0 0 1 0.65])
                         (g/plot ps-mag :position [0 0.65 1 1] :newpage false))))
(note-hiccup [:image {:src "10.11.png"}])

(note-md "### Figure 10.12")
(note-void (plot->file (str target-path "10.12.png")
                       (-> '(formula (sqrt stations) Mag)
                           (lat/bwplot quakes
                                       :scales {:x {:limits (-> quakes ($ 'Mag) base/levels base/as-character)
                                                    :rot 60}}
                                       :xlab "Magnitude (Richter)",
                                       :ylab '(expression (sqrt "Number of stations"))))))
(note-hiccup [:image {:src "10.12.png"}])

(note-md "### Figure 10.13")
(note-void (plot->file (str target-path "10.13.png")
                       (-> '(formula nil (| (sqrt stations) Mag))
                           (lat/qqmath quakes
                                       :type ["p" "g"]
                                       :pch "." :cex 3
                                       :prepanel lat/prepanel-qqmathline
                                       :aspect "xy"
                                       :strip (lat/strip-custom :strip.levels true
                                                                :strip.names false)
                                       :xlab "Standard normal quantiles" 
                                       :ylab '(expression (sqrt "Number of stations"))))))
(note-hiccup [:image {:src "10.13.png"}])

(note-md "### Figure 10.14")
(note-void (plot->file (str target-path "10.14.png")
                       (-> '(formula (sqrt stations) mag)
                           (lat/xyplot quakes
                                       :cex 0.6
                                       :panel lat/panel-bwplot
                                       :horizontal false
                                       :box.ratio 0.05
                                       :xlab "Magnitude (Richter)"
                                       :ylab '(expression (sqrt "Number of stations"))))))
(note-hiccup [:image {:src "10.14.png"}])

(note-md "### Figure 10.15")

(note-void
 (def state-density (let [sd (base/data-frame :name state-name
                                              :area (r/bra state-x77 nil "Area")
                                              :population (r/bra state-x77 nil "Population")
                                              :region state-region)]
                      ($<- sd 'density (base/with sd '(/ population area))))))

(note-void (plot->file (str target-path "10.15.png")
                       (-> '(formula (reorder name density) density)
                           (lat/dotplot state-density
                                        :xlab "Population Density (thousands per square mile)"))))
(note-hiccup [:image {:src "10.15.png"}])

(note-md "### Figure 10.16")

(note-void
 (def state-density ($<- state-density 'Density
                         (lat/shingle ($ state-density 'density)
                                      :intervals (base/rbind [0 0.2] [0.2 1])))))

(note-void (plot->file (str target-path "10.16.png")
                       (-> '(formula (reorder name density) (| density Density))
                           (lat/dotplot state-density
                                        :strip false :latout [2 1] :levels.fos (r/colon 1 50)
                                        :scales {:x "free"} :between {:x 0.5}
                                        :xlab "Population Density (thousands per square mile)"
                                        :par.settings {:layout.widths {:panel [2 1]}}))))
(note-hiccup [:image {:src "10.16.png"}])

(note-md "### Figure 10.17")

(note-void
 (def cut-and-stack (r '(function [x :number 6 :overlap 0.1 :type "l" :xlab "Time"
                                   :ylab (deparse (substitute x)) ...]
                                  (<- time (if (is.ts x)
                                             (time x)
                                             (seq_along x)))
                                  (<- Time (equal.count (as.numeric time) :number number :overlap overlap))
                                  (xyplot (formula (as.numeric x) (| time Time))
                                          :type type :xlab xlab :ylab ylab
                                          :default.scales {:x {:relation "free"}
                                                           :y {:relation "free"}}
                                          ...)))))

(note-void (plot->file (str target-path "10.17.png")
                       (cut-and-stack (r/bra EuStockMarkets nil "DAX")
                                      :aspect "xy"
                                      :scales {:x {:draw false}
                                               :y {:rot 0}}
                                      :ylab "EuStockMarkets[,\\\"DAX\\\"]")))
(note-hiccup [:image {:src "10.17.png"}])

(note-md "### Figure 10.18")

(note-void (def bdp1 (-> '(formula (as.character variety)
                                   (| yield (as.character site)))
                         (lat/dotplot lat/barley
                                      :groups 'year :layout [1 6]
                                      :auto.key {:space "top"
                                                 :columns 2}
                                      ;; :strip false :strip.left true
                                      :aspect "fill")))

           (def bdp2 (-> '(formula variety (| yield site))
                         (lat/dotplot lat/barley
                                      :groups 'year :layout [1 6]
                                      :auto.key {:space "top"
                                                 :columns 2}
                                      ;; :strip false :strip.left true
                                      ))))

(note-void (plot->file (str target-path "10.18.png")
                       (fn []
                         (g/plot bdp1 :split [1 1 2 1])
                         (g/plot bdp2 :split [2 1 2 1] :newpage false))))
(note-hiccup [:image {:src "10.18.png"}])

(note-md "### Figure 10.19")

(note-void
 (def state-density (let [sd (base/data-frame :name state-name
                                              :area (r/bra state-x77 nil "Area")
                                              :population (r/bra state-x77 nil "Population")
                                              :region state-region)]
                      ($<- sd 'density (base/with sd '(/ population area))))))

(note-void (plot->file (str target-path "10.19.png")
                       (-> '(formula (reorder name density)
                                     (* 1000 density))
                           (lat/dotplot state-density
                                        :scale {:x {:log 10}}
                                        :xlab "Density (per square mile)"))))
(note-hiccup [:image {:src "10.19.png"}])

(note-md "### Figure 10.20")

(note-void
 (def state-density (-> state-density
                        ($<- 'region (base/with state-density '(reorder region density median)))
                        ($<- 'name (base/with state-density '(reorder (reorder name density) (as.numeric region)))))))

(note-void (plot->file (str target-path "10.20.png")
                       (-> '(formula name (| (* 1000 density) region))
                           (lat/dotplot state-density
                                        :strip false :strip.left true
                                        :layout [1 4]
                                        :scales {:x {:log 10}
                                                 :y {:relation "free"}}
                                        :xlab "Density (per square mile)"))))
(note-hiccup [:image {:src "10.20.png"}])

(note-md "### Figure 10.21")

(note-md "`late/resizePanels` works only when using viewport")

(note-md "### Figure 10.22")
(note-void (plot->file (str target-path "10.22.png")
                       (-> '(formula rate.male (| rate.female state))
                           (lat/xyplot late/USCancerRates
                                       :aspect "iso" :pch "." :cex 2
                                       :index.cond '(function [x y] (median (- y x) :na.rm true))
                                       :scales {:log 2 :at [75 150 300 600]}
                                       :panel '(function [...]
                                                         (panel.grid :h -1 :v -1)
                                                         (panel.abline 0 1)
                                                         (panel.xyplot ...))))))
(note-hiccup [:image {:src "10.22.png"}])

(note-md "### Figure 10.23")

(note-def
 (def strip-style4 (r '(function [... style]
                                 (strip.default ... :style 4)))))

(note-void (plot->file (str target-path "10.23.png")
                       (-> '(formula nil (| gcsescore (factor score)))
                           (lat/qqmath r.mlmRev/Chem97
                                       :groups 'gender
                                       :type ["l" "g"] :aspect "xy"
                                       :auto.key {:points false :lines true :columns 2}
                                       :f.value (stats/ppoints 100) :strip strip-style4))))

(note-hiccup [:image {:src "10.23.png"}])

(note-md "### Figure 10.24")

(note-def
 (def strip-combined (r '(function [which.given which.panel factor.levels ...]
                                   (if (== which.given 1)
                                     (do (panel.rect 0 0 1 1 :col "grey90" :border 1)
                                         (panel.text :x 0 :y 0.5 :pos 4
                                                     :lab (bra factor.levels (bra which.panel which.given)))))
                                   (if (== which.given 2)
                                     (panel.text :x 1 :y 0.5 :pos 2
                                                 :lab (bra factor.levels (bra which.panel which.given))))))))

(note-void (plot->file (str target-path "10.24.png")
                       (-> '(formula nil (+ (| gcsescore (factor score)) gender))
                           (lat/qqmath r.mlmRev/Chem97
                                       :f.value (stats/ppoints 100)
                                       :type ["l" "g"] :aspect "xy"
                                       :strip strip-combined
                                       :par.strip.text {:lines 0.5}
                                       :xlab "Standard normal quantiles"
                                       :ylab "Average GCSE score"))))
(note-hiccup [:image {:src "10.24.png"}])

(note-md "### Figure 10.25")

(note-void
 (def barley (let [morris (r/r== ($ lat/barley 'site) "Morris")]
               (r `(<- (bra ($ barley year) ~morris)
                       (ifelse (== (bra ($ barley year) ~morris) "1931") "1932" "1931")))
               (r 'barley))))

(note-void (plot->file (str target-path "10.25.png")
                       (-> '(formula (sqrt (abs (residuals (lm (formula yield (+ variety year site))))))
                                     site)
                           (lat/stripplot :data barley
                                          :groups 'year
                                          :jitter.data true
                                          :auto.key {:points true :lines true :columns 2}
                                          :type ["p" "a"]
                                          :fun stats/median
                                          :ylab (r "(expression(abs('Residual Barley Yield')^{1/2}))")))))
(note-hiccup [:image {:src "10.25.png"}])

(note-md :Chapter-11 "## Chapter 11")

(note (r->clj (r.utils/methods :class "trellis")))
(note (r->clj (r.utils/methods :class "shingle")))
(note (r->clj (r.utils/methods :generic.function "barchart")))

(note-md "### Figure 11.1")

(note-void (def dp-uspe (lat/dotplot (base/t USPersonalExpenditure)
                                     :groups false
                                     :index.cond '(function [x y] (median x))
                                     :layout [1 5]
                                     :type ["p" "h"]
                                     :xlab "Expenditure (billion dollars)"))
           (def dp-uspe-log (lat/dotplot (base/t USPersonalExpenditure)
                                         :groups false
                                         :index.cond '(function [x y] (median x))
                                         :layout [1 5]
                                         :scales {:x {:log 2}}
                                         :xlab "Expenditure (billion dollars)")))

(note-void (plot->file (str target-path "11.1.png")
                       (fn []
                         (g/plot dp-uspe :split [1 1 2 1] :more true)
                         (g/plot dp-uspe-log :split [2 1 2 1] :more false))))
(note-hiccup [:image {:src "11.1.png"}])

(note-md "### Figure 11.2")

(note-void
 (def state (let [state (base/data-frame state-x77 state-region state-name)]
              ($<- state 'state.name
                   (base/with state '(reorder (reorder state.name Frost)
                                              (as.numeric state.region))))))
 (def dpfrost (-> '(formula state.name (| Frost (reorder state.region Frost)))
                  (lat/dotplot :data state
                               :layout [1 4]
                               :scales {:y {:relation "free"}}))))

(note (summary dpfrost))

(note-void (plot->file (str target-path "11.2.png")
                       #(g/plot dpfrost
                                :panel.height {:x [16 13 9 12] :unit "null"})))
(note-hiccup [:image {:src "11.2.png"}])

(note-md "### Figure 11.3")
(note-void (plot->file (str target-path "11.3.png")
                       (r/bra (stats/update (lat/trellis-last-object)
                                            :layout [1 1]) 2)))
(note-hiccup [:image {:src "11.3.png"}])

(note-md "### Figure 11.4")

(note-void
 (def npanel 12)
 (def rot {:z (base/seq 0 30 :length npanel)
           :x (base/seq 0 -80 :length npanel)})

 (def quake-locs (-> '(formula depth (+ long lat))
                     (lat/cloud quakes :pch "." :cex 1.5
                                :panel '(function [... screen]
                                                  (<- pn (panel.number))
                                                  (panel.cloud ... :screen {:z (bra ~(:z rot) pn)
                                                                            :x (bra ~(:x rot) pn)}))
                                :xlab nil :ylab nil :zlab nil
                                :scales {:draw false}
                                :zlim [690 30]
                                :par.settings {:axis.line {:col "transparent"}}))))

(note-void (plot->file (str target-path "11.4.png")
                       (r/bra quake-locs (base/rep 1 npanel))))
(note-hiccup [:image {:src "11.4.png"}])

(note-md "### Figure 11.5")

(note-void
 (def chem-qq (-> '(formula gender (| gcsescore (factor score)))
                  (lat/qq r.mlmRev/Chem97
                          :f.value (stats/ppoints 100)
                          :strip (lat/strip-custom :style 5)))))

(note-void (plot->file (str target-path "11.5.png")
                       (lat/tmd chem-qq)))
(note-hiccup [:image {:src "11.5.png"}])

(note-md "### Figure 11.6")

(note-void
 (def baxy (do (<- 'baxy (-> '(formula (log10 counts)
                                       (+ (| hour month) weekday))
                             (lat/xyplot late/biocAccess
                                         :type ["p" "a"] :as.table true
                                         :pch "." :cex 2 :col.line "black")))
               (r '(<- ($ (dimnames baxy) month) (bra month.name [1 2 3 4 5])))
               (r 'baxy))))

(note-void (plot->file (str target-path "11.6.png")
                       (late/useOuterStrips baxy)))
(note-hiccup [:image {:src "11.6.png"}])

(note-md :Chapter-12-skipped "## Chapter 12 (skipped)")

(note-md "Interactive charts - skipped.")

(note-md :Chapter-13 "## Chapter 13")

(note-md "### Figure 13.1")

(note-void
 (def panel-hypotrochoid (r '(function [r d :cycles 10 :density 30]
                                       (if (missing r) (<- r (runif 1 0.25 0.75)))
                                       (if (missing d) (<- d (runif 1 (* r 0.25) r)))
                                       (<- t (* 2 pi (seq 0 cycles :by (/ 1 density))))
                                       (<- x (+ (* (- 1 r) (cos t))
                                                (* d (cos (/ (* (- 1 r) t) r)))))
                                       (<- y (- (* (- 1 r) (sin t))
                                                (* d (sin (/ (* (- 1 r) t) r)))))
                                       (panel.lines x y))))

 (def panel-hypocycloid (r '(function [x y :cycles x :density 30]
                                      (~panel-hypotrochoid :r (/ x y) :d (/ x y) :cycles cycles :density density))))

 (def prepanel-hypocycloid (r '(function [x y] {:xlim [-1 1] :ylim [-1 1]})))
 (def grid (let [g (base/data-frame :p (r/colon 11 30) :q 10)]
             ($<- g 'k (base/with g '(factor (/ p q)))))))

(note-void (plot->file (str target-path "13.1.png")
                       (-> '(formula p (| q k))
                           (lat/xyplot grid
                                       :aspect 1 :scales {:draw false}
                                       :prepanel prepanel-hypocycloid
                                       :panel panel-hypocycloid))))
(note-hiccup [:image {:src "13.1.png"}])

(note-md "### Figure 13.2")

(note-void
 (def p (-> '(formula [-1 1] [-1 1])
            (lat/xyplot :aspect 1 :cycles 15
                        :scales {:draw false} :xlab "" :ylab ""
                        :panel panel-hypotrochoid))))

(note-void (base/set-seed 20070706))

(note-void (plot->file (str target-path "13.2.png")
                       (r/bra p (base/rep 1 42))))
(note-hiccup [:image {:src "13.2.png"}])

(note-md "### Figure 13.3")

(note-void
 (def prepanel-ls (r '(function [x :n 50 ...]
                                (<- fit (logspline x))
                                (<- xx (do.breaks (range x) n))
                                (<- yy (dlogspline xx fit))
                                {:ylim [0 (max yy)]})))

 (def panel-ls (r '(function [x :n 50 ...]
                             (<- fit (logspline x))
                             (<- xx (do.breaks (range x) n))
                             (<- yy (dlogspline xx fit))
                             (panel.lines xx yy ...))))

 (def faithful ($<- faithful 'Eruptions (lat/equal-count ($ faithful 'eruptions ) 4))))

(note-void (plot->file (str target-path "13.3.png")
                       (-> '(formula nil (| waiting Eruptions))
                           (lat/densityplot :data faithful
                                            :prepanel prepanel-ls
                                            :panel panel-ls))))
(note-hiccup [:image {:src "13.3.png"}])

(note-md "### Figure 13.4")

(note-void
 (def panel-bwtufte (r '(function [x y :coef 1.5 ...]
                                  (<- x (as.numeric x))
                                  (<- ux (sort (unique x)))
                                  (<- blist (tapply y (factor x :levels ux) boxplot.stats
                                                    :coef coef :do.out false))
                                  (<- blist.stats (t (sapply blist "[[" "stats")))
                                  (<- blist.out (lapply blist "[[" "out"))
                                  (panel.points :y (bra blist.stats nil 3)
                                                :x ux :pch 16 ...)
                                  (panel.segments :x0 (rep ux 2)
                                                  :y0 [(bra blist.stats nil 1)
                                                       (bra blist.stats nil 5)]
                                                  :x1 (rep ux 2)
                                                  :y1 [(bra blist.stats nil 2)
                                                       (bra blist.stats nil 4)]
                                                  ...)))))

(note-void (plot->file (str target-path "13.4.png")
                       (-> '(formula (** gcsescore 2.34)
                                     (| gender (factor score)))
                           (lat/bwplot r.mlmRev/Chem97
                                       :panel panel-bwtufte
                                       :layout [6 1]
                                       :ylab "Transformed GCSE score"))))
(note-hiccup [:image {:src "13.4.png"}])

(note-md "### Figure 13.5")

(note-void
 (def cor-cars93 (stats/cor (r/bra r.MASS/Cars93
                                   nil
                                   (r/r! (base/sapply r.MASS/Cars93 base/is-factor)))
                            :use "pair"))

 (def ord (-> cor-cars93
              stats/dist
              stats/hclust
              stats/as-dendrogram
              stats/order-dendrogram))

 (def panel-corrgram (r '(function [x y z subscripts at :level 0.9 :label false ...]
                                   (require "ellipse" :quietly true)
                                   (<- x (bra (as.numeric x) subscripts))
                                   (<- y (bra (as.numeric y) subscripts))
                                   (<- z (bra (as.numeric z) subscripts))
                                   (<- zcol (level.colors z :at at ...))
                                   (for [i (seq :along z)]
                                     (<- ell (ellipse (bra z i) :level level :npoints 50
                                                      :scale [0.2 0.2] :centre [(bra x i) (bra y i)]))
                                     (panel.polygon ell :col (bra zcol [i]) :border (bra zcol i) ...))
                                   (if label
                                     (panel.text :x x :y y :lab (* 100 (round z 2)) :cex 0.8
                                                 :col (ifelse (< z 0) "white" "black")))))))

(note-void (plot->file (str target-path "13.5.png")
                       (lat/levelplot (r/bra cor-cars93 ord ord)
                                      :at (lat/do-breaks [-1.01 1.01] 20)
                                      :xlab nil :ylab nil :colorkey {:space "top"}
                                      :scales {:x {:rot 90}}
                                      :panel panel-corrgram :label true)))
(note-hiccup [:image {:src "13.5.png"}])

(note-md "### Figure 13.6")

(note-void
 (def panel-corrgram-2 (r '(function [x y z subscripts :at (pretty z) :scale 0.8 ...]
                                     (require "grid" :quietly true)
                                     (<- x (bra (as.numeric x) subscripts))
                                     (<- y (bra (as.numeric y) subscripts))
                                     (<- z (bra (as.numeric z) subscripts))
                                     (<- zcol (level.colors z :at at ...))
                                     (for [i (seq :along z)]
                                       (<- lims (range 0 (bra z i)))
                                       (<- tval (* 2 pi (seq :from (bra lims 1)
                                                             :to (bra lims 2)
                                                             :by 0.01)))
                                       (grid.circle :x (bra x i) :y (bra y i)
                                                    :r (* 0.5 scale)
                                                    :default.units "native")
                                       (grid.polygon :x (+ (bra x i)
                                                           (* 0.5 scale [0 (sin tval)]))
                                                     :y (+ (bra y i)
                                                           (* 0.5 scale [0 (cos tval)]))
                                                     :default.units "native"
                                                     :gp (gpar :fill (bra zcol i))))))))

(note-void (plot->file (str target-path "13.6.png")
                       (lat/levelplot (r/bra cor-cars93 ord ord)
                                      :at (lat/do-breaks [-1.01 1.01] 101)
                                      :xlab nil :ylab nil
                                      :panel panel-corrgram-2
                                      :scale {:x {:rot 90}}
                                      :colorkey {:space "top"}
                                      :col.regions (dev/colorRampPalette ["red" "white" "blue"]))))
(note-hiccup [:image {:src "13.6.png"}])

(note-md "### Figure 13.7")

(note-void
 (def panel-3d-contour (r '(function [x y z rot.mat distance :nlevels 20 zlim.scaled ...]
                                     (<- add.line (trellis.par.get "add.line"))
                                     (panel.3dwire x y z rot.mat distance :zlim.scaled zlim.scaled ...)
                                     (<- clines (contourLines x y (matrix z :nrow (length x) :byrow true) :nlevels nlevels))
                                     (for [ll clines]
                                       (<- m (ltransform3dto3d (rbind ll$x ll$y (bra zlim.scaled 2)) rot.mat distance))
                                       (panel.lines (bra m 1 nil)
                                                    (bra m 2 nil)
                                                    :col add.line$col
                                                    :lty add.line$lty
                                                    :lwd add.line$lwd))))))

(note-void (plot->file (str target-path "13.7.png")
                       (lat/wireframe 'volcano
                                      :zlim [90 250] :nlevels 10
                                      :aspect [61/87 0.3] :panel.aspect 0.6
                                      :shade true
                                      :panel.3d.wireframe panel-3d-contour
                                      :screen {:z 20 :x -60})))
(note-hiccup [:image {:src "13.7.png"}])

(note-md "### Figure 13.8")

(note-void
 (def county-map (r.maps/map "county" :plot false :fill true)))

(note (base/summary county-map))

(note-void
 (def ancestry (as-> (base/subset late/ancestry '(! (duplicated county))) a
                 (base/rownames<- a ($ a 'county))))
 (def freq (base/table ($ ancestry 'top)))
 (def keep-names (r/bra (base/names freq) (r/r> freq 10)))
 (def ancestry ($<- ancestry 'mode (base/with ancestry '(factor (ifelse (%in% top ~keep-names) top "Other")))))
 (def modal-ancestry (r/bra ancestry ($ county-map 'names) "mode"))
 (def colors (r.RColorBrewer/brewer-pal :n (base/nlevels ($ ancestry 'mode)) :name "Pastel1")))

(note-void (plot->file (str target-path "13.8.png")
                       (-> '(formula y x)
                           (lat/xyplot county-map
                                       :aspect "iso"
                                       :scales {:draw false}
                                       :xlab nil :ylab nil
                                       :par.settings {:axis.line {:col "transparent"}}
                                       :col (r/bra colors modal-ancestry)
                                       :border 'NA
                                       :panel lat/panel-polygon
                                       :key {:text [:!list (base/levels modal-ancestry) :adj 1]
                                             :rectangles {:col colors}
                                             :x 1 :y 0 :corner [1 0]}))))
(note-hiccup [:image {:src "13.8.png"}])

(note-md "### Figure 13.9")

(note-void
 (def rad (r '(function [x] (/ (* pi x) 180))))

 (def county-map (-> county-map
                     ($<- 'xx (base/with county-map '(* (cos (~rad x))
                                                        (cos (~rad y)))))
                     ($<- 'yy (base/with county-map '(* (sin (~rad x))
                                                        (cos (~rad y)))))
                     ($<- 'zz (base/with county-map '(sin (~rad y))))))

 (def panel-3dpoly (r '(function [x y z :rot.mat (diag 4) distance ...]
                                 (<- m (ltransform3dto3d (rbind x y z) rot.mat distance))
                                 (panel.polygon :x (bra m 1 nil)
                                                :y (bra m 2 nil)
                                                ...))))

 (def aspect (base/with county-map '[(diff (range yy :na.rm true))
                                     (/ (diff (range zz :na.rm true))
                                        (diff (range xx :na.rm true)))])))


(note-void (plot->file (str target-path "13.9.png")
                       (-> '(formula zz (* xx yy))
                           (lat/cloud county-map
                                      :par.box {:col "grey"}
                                      :aspect aspect :panel.aspect 0.6 :lwd 0.2
                                      :panel.3d.cloud panel-3dpoly
                                      :col (r/bra colors modal-ancestry)
                                      :screen {:z 10 :x -30}
                                      :key {:text [:!list (base/levels modal-ancestry) :adj 1]
                                            :rectangles {:col colors}
                                            :space "top" :columns 4}
                                      :scales {:draw false} :zoom 1.1
                                      :xlab nil :ylab nil :zlab nil)) :width 600))
(note-hiccup [:image {:src "13.9.png"}])

(note-md "### Figure 13.10")

(note-void
 (def rng (base/with late/USCancerRates '(range rate.male rate.female :finite true)))
 (def nbreaks 50)
 (def breaks (base/exp (lat/do-breaks (base/log rng) nbreaks))))

(note-void (plot->file (str target-path "13.10.png")
                       (-> '(formula (rownames USCancerRates)
                                     (+ rate.male rate.female))
                           (late/mapplot :data late/USCancerRates
                                         :breaks breaks
                                         :scales {:draw false}
                                         :xlab ""
                                         :map (r.maps/map "county" :plot false :fill true :projection "tetra")
                                         :main "Average yearly deaths due to cancer per 100000"))))
(note-hiccup [:image {:src "13.10.png"}])

(note-md :Chapter-14 "Chapter 14")

(note-md "### Figure 14.1")
(note-void (plot->file (str target-path "14.1.png")
                       (lat/xyplot sunspot-year
                                   :aspect "xy"
                                   :strip false :strip.left true
                                   :cut {:number 4 :overlap 0.05})))
(note-hiccup [:image {:src "14.1.png"}])

(note-md "### Figure 14.2")

(note-void
 (def ssd (-> late/biocAccess
              ($ 'counts)
              (r/bra (r/colon 1 (* 24 30 2)))
              (stats/ts :frequency 24)
              (stats/stl "periodic"))))

(note-void (plot->file (str target-path "14.2.png")
                       (lat/xyplot ssd :xlab "Time (Days)")))
(note-hiccup [:image {:src "14.2.png"}])


(note-md "### Figure 14.3")

(note-void (r/data :GvHD "flowCore"))

(note-void (plot->file (str target-path "14.3.png")
                       (-> '(formula Visit (| ~(symbol "`FSC-H`") Patient))
                           (r.flowViz/densityplot :data GvHD))))
(note-hiccup [:image {:src "14.3.png"}])

(note-md "### Figure 14.4")

(note-void
 (r/data 'NHANES "hexbin"))

(note-void (plot->file (str target-path "14.4.png")
                       (-> '(formula Hemoglobin (| TIBC Sex))
                           (r.hexbin/hexbinplot :data NHANES))))
(note-hiccup [:image {:src "14.4.png"}])

(note-md "### Figure 14.5")

(note-void
 (def panel-piechart (r '(function [x y :labels (as.character y)
                                    :edges 200 :radius 0.8 :clockwise false
                                    :init.angle (if clockwise 90 0) :density nil
                                    :angle 45 :col superpose.polygon$col
                                    :border superpose.polygon$border
                                    :lty superpose.polygon$lty ...]
                                   (stopifnot (require "gridBase"))
                                   (<- superpose.polygon (trellis.par.get "superpose.polygon"))
                                   (<- opar (par :no.readonly true))
                                   (on.exit (par opar))
                                   (if (> (panel.number) 1) (par :new true))
                                   (par :fig (gridFIG) :omi [0 0 0 0] :mai [0 0 0 0])
                                   (pie (as.numeric x) :labels labels :edges edges :radius radius
                                        :clockwise clockwise :init.angle init.angle :angle angle
                                        :density density :col col :border border :lty lty)))))
(note-void
 (def piechart (r '(function [x :data nil :panel ~panel-piechart ...]
                             (<- ocall (sys.call (sys.parent)))
                             (brabra<- ocall 1 :value (quote piechart))
                             (<- ccall (match.call))
                             (<- ccall$data data)
                             (<- ccall$panel panel)
                             (<- ccall$default.scales {:draw false})
                             (brabra<- ccall 1 :value (quote barchart))
                             (<- ans (eval.parent ccall))
                             (<- ans$call ocall)
                             ans))))

(note-void (plot->file (str target-path "14.5.png")
                       (do (g/par :new true)
                           (piechart VADeaths :groups false :xlab ""))))
(note-hiccup [:image {:src "14.5.png"}])

(comment (dev/x11 :width 9))
(comment (dev/dev-off))

(comment (notespace.v2.note/compute-this-notespace!))

(comment (r/discard-all-sessions))
