(ns clojisr-examples.r-by-example
  (:require [notespace.v1.note :as note
             :refer [note note-void note-md note-as-md note-hiccup note-as-hiccup]]))

(note-md "# 'R by Example' by Jim Albert and Maria Rizzo - read-along")

(note-md "Code from the book ported to `clojisr` library. Read the book and run a code in Clojure.")

(note-void :Setup)

(note-md "## Setup")

(note-md "Imports from `clojisr` library.")

(note-void (require '[clojisr.v1.r :as r :refer [r r->clj clj->r r+ colon bra bra<-]]
                    '[clojisr.v1.require :refer [require-r]]
                    '[clojisr.v1.applications.plotting :refer [plot->file]]
                    '[tech.ml.dataset :as dataset]
                    '[notespace.v1.util :refer [check]]))

(note-md "Import basic R packages.")

(note-void (require-r '[base :as base :refer [$ <- $<-]]
                      '[utils :as u]
                      '[stats :as stats]
                      '[graphics :as g]
                      '[datasets :refer :all]))

(note-md "We will also use `def-r` macro (not shown here) which helps to define the same things on both sides (Clojure and R).")

(defmacro def-r [name & r]
  `(do
     (def ~name ~@r)
     (<- '~(symbol name) ~name)))

(note-void :Chapter-1)

(note-md "## Chapter 1 - Introduction")

(note-md "### 1.1.1 - Preliminaries")

(note-md "Various ways to call R backend.")

(note (let [x [109.0 65.0 22.0 3.0 1.0]
            x1 (r->clj (r [109.0 65 22 3 1]))
            x2 (r->clj (r "c(109, 65, 22, 3, 1)"))
            x3 (r->clj (r '[c 109.0 65.0 22.0 3.0 1.0]))
            x4 (r->clj (base/c 109.0 65.0 22.0 3.0 1.0))
            x5 (r->clj (r x))]
        (check = x1 x2 x3 x4 x5 x)))

(note-md "Assigment to a var on R side. Three variants.")

(note (do (r '(= x1 (c 109 65 22 3 1)))
          (r '(<- x2 (c 109 65 22 3 1)))
          (<- 'x3 [109 65 22 3 1])
          (check = (r->clj (r 'x1)) (r->clj (r 'x2)) (r->clj (r 'x3)))))

(note-md "We will use `def-r` helper to define the same thing in Clojure and R.")

(note (def-r x [109 65 22 3 1]))
(note (check = x (r->clj (r 'x))))

(note-md "Below `y` on Clojure and R sides represent the same R object. `stats/rpois` is R function.")

(note (def-r y (stats/rpois 200 :lambda 0.61)))
(note (check =
             (take 10 (r->clj y))
             (take 10 (r->clj (r 'y)))))

(note-md "Usually it's enough to have variable only on Clojure side.")

(note-md "### 1.1.2 - Basic operations")

(note-md "#### Example 1.1 - Temperature data")

(note (def temps [51.9 51.8 51.9 53]))

(note (r ['- temps 32]))
(note (r ['* 5/9 ['- temps 32]]))

(note-md "We can define helper functions for primitive binary operations used above")

(note-void (def r- (r "`-`"))
           (def r* (r "`*`")))

(note (r- temps 32))
(note (r* 5/9 (r- temps 32)))

(note (def CT [48 48.2 48 48.7]))

(note (r- temps CT))

(note-md "#### Example 1.2 - President's heights")

(note-void (def winner [185 182 182 188 188 188 185 185 177 182 182 193 183 179 179 175])
           (def opponent [175 193 185 187 188 173 180 177 183 185 180 180 182 178 178 173]))

(note (let [l1 (first (r->clj (base/length winner)))
            l2 (count winner)]
        (check = l1 l2)))

(note (def year (base/seq :from 2008 :to 1948 :by -4)))

(note-md "or")

(note (def year (base/seq 2008 1948 -4)))

(note-md "Modifying data")

(note (def winner-var1 (-> winner
                           (bra<- 4 189)
                           (bra<- 5 189))))

(note (def winner-var2 (bra<- winner (colon 4 5) 189)))

(note (check = (r->clj winner-var1) (r->clj winner-var2)))

(note (def winner winner-var1))

(note (base/mean winner))
(note (base/mean opponent))

(note (def difference (r- winner opponent)))

(note-md "We have to explicitly provide column names.")

(note (base/data-frame :year year :winner winner :opponent opponent :difference difference))

(note (def taller-won (r ['> winner opponent])))

(note (base/table taller-won))

(note (def rdiv (r "`/`")))
(note (-> taller-won
          (base/table)
          (rdiv 16)
          (r* 100)))

(def target-path (second (re-find #"(.*)/[^/]*" (notespace.v1.note/ns->out-filename *ns*))))

(note-void (plot->file (str target-path "/ch1ex2.png") #(g/barplot (base/rev difference)
                                                                   :xlab "Election years 1948 to 2008",
                                                                   :ylab "Height difference in cm")))
(note-hiccup [:image {:src "ch1ex2.png"}])

(note-void (plot->file (str target-path "/ch1ex2b.png") #(g/plot winner opponent)))
(note-hiccup [:image {:src "ch1ex2b.png"}])

(note-md "#### Example 1.3 - horsekicks")

(note-void (def k [0 1 2 3 4])
           (def x [109 65 22 3 1]))

(note-void (plot->file (str target-path "/ch1ex3.png") #(g/barplot x :names.arg k)))
(note-hiccup [:image {:src "ch1ex3.png"}])

(note (def rpow (r "`^`")))

(note (def p (rdiv x (base/sum x))))
(note (def rr (base/sum (r* p k))))
(note (def v (-> (r- k rr)
                 (rpow 2)
                 (r* x)
                 (base/sum)
                 (rdiv 199))))

(note (def f1 (-> (r- rr)
                  (base/exp)
                  (r* (rpow rr k))
                  (rdiv (base/factorial k))
                  (r->clj))))

(note (def f2 (r->clj (stats/dpois k rr))))

(note (def f f2))

(note-md "Expected counts")
(note (r->clj (base/floor (r* 200 f))))
(note-md "Observed counts")
(note x)

(note (base/cbind k p f))

(note-md "### 1.1.3 - R Scripts")

(note-md "#### Example 1.4 - Simulated horsekick data")

(note (def y (stats/rpois 200 :lambda 0.61)))

(note-md "Table of sample frequencies")
(note (def kicks (base/table y)))

(note-md "Sample proportions")
(note (rdiv kicks 200))

(note (def theoretical (-> kicks
                           r->clj
                           count
                           range
                           (stats/dpois :lambda 0.61))))
(note (def sample (rdiv kicks 200)))
(note (base/cbind :theoretical  theoretical :sample sample))

(note (base/mean y))
(note (stats/var y))

(note-md "### 1.1.4 - The R Help System")

(note-md "Unfortunately `help` or `example` commands do not work")

(note-md "## 1.2 - Functions")

(note-md "Two ways to define functions.")

(note-md "R string")
(note (def f1 (r "function(...) {1}")))
(note (f1))
(note-md "Clojure style")
(note (def f2 (r '(function [...] 1))))
(note (f2))

(note-md "#### Example 1.5 - function definition")

(note (def var-n (r '(function [x]
                               (= v (var x))
                               (= n (NROW x))
                               (/ (* (- n 1) v) n)))))

(note (stats/var temps))
(note (var-n temps))

(note-md "#### Example 1.6 - functions as arguments")

(note (def f (r "function(x, a=1, b=1) x^(a-1) * (1-x)^(b-1)")))
(note (let [x (base/seq 0 1 0.2)]
        (f x :a 2 :b 2)))

(note (stats/integrate f :lower 0 :upper 1 :a 2 :b 2))

(note (base/beta 2 2))

(note-md "#### Example 1.7 - graphing a function using `curve`")

(note-void (plot->file (str target-path "/ch1ex7.png") #(g/curve '(* x (- 1 x))
                                                                 :from 0 :to 1 :ylab "f(x)")))
(note-hiccup [:image {:src "ch1ex7.png"}])


(note-md "## 1.3 - Vectors and Matrices")

(note-md "#### Example 1.8 - Class mobility")

(note (def probs [0.45 0.05 0.01 0.48 0.70 0.50 0.07 0.25 0.49]))
(note (def P (base/matrix probs :nrow 3 :ncol 3)))
(note-md "Currently matrix is flat array in Clojure")
(note (r->clj P))

(note (def P (let [names ["lower" "middle" "upper"]]
               (-> P
                   (base/rownames<- names)
                   (base/colnames<- names)))))

(note (base/rowSums P))
(note (base/apply P :MARGIN 1 :FUN base/sum))

(note (def P2 (base/%*% P P)))

(note (bra P2 1 3))
(note (bra P2 1 (r/empty-symbol)))

(note (let [P4 (base/%*% P2 P2)
            P8 (base/%*% P4 P4)]
        P8))


(note (def Q (base/matrix [0.45 0.48 0.07
                           0.05 0.70 0.25
                           0.01 0.50 0.49] :nrow 3 :ncol 3 :byrow true)))

(note-md "## 1.4 - Data Frames")

(note-md "### 1.4.1 - Introduction to data frames")

(note-md "#### Example 1.9 - USArrests")

(note USArrests)
(note (r->clj USArrests))
(note (u/head USArrests))
(note (base/NROW USArrests))
(note (base/dim USArrests))
(note (base/names USArrests))
(note-md "`str` doesn't return anything, try `ls-str` instead.")
(note (u/ls-str USArrests))
(note (base/any (base/is-na USArrests)))

(note-md "### 1.4.2 - Working with a data frame")

(note (base/summary USArrests))
(note (bra USArrests "California" "Murder"))
(note (bra USArrests "California" (r/empty-symbol)))
(note ($ USArrests 'Assault))

(note-void (plot->file (str target-path "/ch1ex9.png") #(g/hist ($ USArrests 'Assault))))
(note-hiccup [:image {:src "ch1ex9.png"}])

(note (require-r '[MASS]))

(note-void (plot->file (str target-path "/ch1ex9b.png") #(r.MASS/truehist ($ USArrests 'Assault))))
(note-hiccup [:image {:src "ch1ex9b.png"}])

(note-void (plot->file (str target-path "/ch1ex9c.png") #(g/hist ($ USArrests 'Assault)
                                                                 :prob true :breaks "scott")))
(note-hiccup [:image {:src "ch1ex9c.png"}])

(note-md "Attach creates symbols on the R side, use Clojure symbols (ex. 'Murder) to access them.")
(note (base/attach USArrests))

(note (def murder-pct (r '(/ (* 100 Murder)
                             (+ (+ Murder Assault) Rape)))))
(note (u/head murder-pct))

#_(note-md "problems with `with`")
#_(note (base/with USArrests :expr '(= murder-pct (/ (* 100 Murder)
                                                     (+ (+ Murder Assault) Rape)))))

(note-void (plot->file (str target-path "/ch1ex9d.png") #(g/plot 'UrbanPop 'Murder)))
(note-hiccup [:image {:src "ch1ex9d.png"}])

(note-void (plot->file (str target-path "/ch1ex9e.png") #(g/pairs USArrests)))
(note-hiccup [:image {:src "ch1ex9e.png"}])

(note (stats/cor 'UrbanPop 'Murder))
(note (stats/cor USArrests))

#_(note-md "## 1.5 - Importing Data")

(note/render-this-ns!)

#_(r/discard-all-sessions)
