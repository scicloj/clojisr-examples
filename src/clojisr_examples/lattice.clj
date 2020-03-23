(ns clojisr-examples.lattice
  "Rework of examples from Lattice book.

  http://lmdvr.r-forge.r-project.org/figures/figures.html"
  (:require [notespace.v2.note :as note
             :refer [note note-void note-md note-as-md note-hiccup note-as-hiccup]]
            [clojisr.v1.r :as r]))

;; Be sure to install following packages:
;;
;; install.packages(c("lattice", "latticeExtra", "mlmrev", "MEMSS", "locfit", "hexbin"))

(note/defkind note-def :def {:render-src?    true
                             :value-renderer (comp notespace.v2.view/value->hiccup var-get)})

(def target-path (notespace.v2.note/ns->out-dir *ns*))

(note-md "# Lattice")

(note-md "Examples from the page: http://lmdvr.r-forge.r-project.org/figures/figures.html")

(note-md :Setup "## Setup")

(note-void (require '[clojisr.v1.r :as r :refer [r+ r r->clj clj->r clj->java java->clj java->r r->java]]
                    '[clojisr.v1.require :refer [require-r]]
                    '[clojisr.v1.applications.plotting :refer [plot->file]]))

(note-void (require-r '[lattice :as lat]
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
                      '[hexbin]
                      '[datasets :refer :all]))

(note (r->clj r.base/version))

#_(dev/x11 :width 9)

;; Chapter 1

(note-md :Chapter-1 "## Chapter 1")

(note (r->clj (stats/xtabs '(formula nil score) :data r.mlmRev/Chem97)))
;; => [3688 3627 4619 5739 6668 6681]

(note-md "### Figure 1.1")

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

(note-md "### Figure 2.2")

(note (r->clj (base/summary (r/bra tp1-oats (r/empty-symbol) 1))))
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

(note-void (plot->file (str target-path "2.2.png") (r/bra tp1-oats (r/empty-symbol) 1)))
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

(note-void (def chem97-mod (base/transform r.mlmRev/Chem97 :gcsescore.trans '("`^`" gcsescore 2.34))))

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
(note-void (plot->file (str target-path "3.12.png") (-> '(formula ("`^`" gcsescore 2.34) (| gender  (factor score)))
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

(note VADeaths)
(note (base/class VADeaths))
(note (r->clj (r.utils/methods "dotplot")))

;; Figure 1
(note-md "### Figure 4.1")
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
                           (lat/xyplot :data (r/bra quakes depth-ord (r/empty-symbol)) :aspect "iso"
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

(note-void (def quakes-ordered (r/bra quakes depth-ord (r/empty-symbol))))

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

(note-void (def depth-breaks (lat/do-breaks (base/range ($ quakes-ordered 'depth)) 50)))

(note-void (def quakes-ordered (-> quakes-ordered
                                   ($<- 'color (lat/level-colors ($ quakes-ordered 'depth)
                                                                 :at depth-breaks
                                                                 :col.regions dev/gray-colors)))))
(note-md "### Figure 5.6")
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
                                                                (panel.fill (bra (~$ (trellis.par.get "strip.background") col) 1))
                                                                (<- type (bra ~types (panel.number)))
                                                                (grid.text :lab (sprintf "%s" type) :x 0.5 :y 0.5)
                                                                (grid.rect))
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
(note-void (plot->file (str target-path "5.19.png")
                       (-> '(formula nil (asinh (bra gvhd10 [3 2 4 1 5])))
                           (lat/parallel :data late/gvhd10
                                         :subset '(== Days 13)
                                         :alpha 0.01 :lty 1))))
(note-hiccup [:image {:src "5.19.png"}])

#_(dev/dev-off)

(comment (notespace.v2.note/compute-this-notespace!))

(comment (r/discard-all-sessions))
