(ns clojisr-examples.r-by-example
  (:require [notespace.v1.note :as note
             :refer [note note-void note-md note-as-md note-hiccup note-as-hiccup]]))

(note-md "# 'R by Example' by Jim Albert and Maria Rizzo - read-along")

(note-md "Code from the book ported to `clojisr` library. Read the book and run a code in Clojure.")
(note-md "Data: http://personal.bgsu.edu/~mrizzo/Rx/Rx-data/")

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

(note-md "Ensure R working folder is the same as Clojure.")

(note-void (base/setwd (.getAbsolutePath (java.io.File. "."))))

(note-md "Options")
(note-void (base/options :width 120 :digits 7))

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
(note (def f1 (r "function(...) {1.0}")))
(note-md "Clojure style")
(note (def f2 (r '(function [...] 1.0))))

(note (check = (r->clj (f1)) (r->clj (f2))))

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

(note-md "#### Example 1.10 - USArrests, cont.")

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

(note-md "## 1.5 - Importing Data")

(note-md "### 1.5.1 - Entering data manually")

(note-md "#### Example 1.11 - Data from a textbook")

(note (def y (let [y1 [22 26]
                   y2 [28 24 29]
                   y3 [29 32 28]
                   y4 [23 24]]
               (concat y1 y2 y3 y4))))

(note (def model (base/c (base/rep "A" 2)
                         (base/rep "B" 3)
                         (base/rep "C" 3)
                         (base/rep "D" 2))))

(note (def mileages (base/data-frame :y y :model model)))

(note-md "### 1.5.2 - Importing data from a text file")

(note-md "#### Example 1.12 - Massachusetts Lunatics")

(note-void (def lunatics1 (u/read-table "data/lunatics.txt" :header true)))
(note (u/head lunatics1))
(note-void (def dat (u/read-table "data/college.txt" :header true :sep "\t")))
(note (u/head dat))
(note-void (def lunatics2 (u/read-table "data/lunatics.csv" :header true :sep ",")))
(note (u/head lunatics2))
(note-void (def lunatics3 (u/read-csv "data/lunatics.csv")))
(note (u/head lunatics3))

(note-md "### 1.5.3 - Data available on the internet")

(note-md "#### Example 1.13 - Digits of PI")

(note-void (def pidigits (u/read-table "http://www.itl.nist.gov/div898/strd/univ/data/PiDigits.dat" :skip 60)))
(note (u/head pidigits))
(note (base/table pidigits))
(note (def prop (rdiv (base/table pidigits) 5000)))
(note (Math/sqrt (/ (* 0.1 0.9) 5000)))
(note (base/sqrt (rdiv (r* 0.1 0.9) 5000)))
(note (def se-hat (base/sqrt (rdiv (r* prop (r- 1.0 prop)) 5000))))
(note (-> (base/rbind :prop prop :se.hat se-hat
                      (r- prop (r* 2.0 se-hat))
                      (r+ prop (r* 2.0 se-hat)))
          (base/round 4)))

(note-void (plot->file (str target-path "/ch1ex13.png") (fn []
                                                          (g/barplot prop :xlab "digit" :ylab "propotion")
                                                          (g/abline :h 0.1))))
(note-hiccup [:image {:src "ch1ex13.png"}])

(note-md "## 1.6 - Packages")

(note-void (r->clj (base/library)))

(note-md "#### Example 1.14 - Using the `bootstrap` package")

(note (try (r 'law)
           (catch Exception e (.getMessage e))))

(note-md "`(u/install-packages \"bootstrap\")`")

(note-void (r->clj (u/data :package "bootstrap")))

(note-void (require-r '[bootstrap]))

(note r.bootstrap/law)
(note (base/colMeans 'law))
(note (stats/cor ($ 'law 'LSAT)
                 ($ 'law 'GPA)))


(note-md "## 1.7 - The R Workspace")

(note (base/ls))
(note (base/source "data/horsekicks.R"))
#_(note (base/ls))
(note-void (base/rm 'v))
(note-void (base/rm :list ["f" "r"]))
(note (r.base/ls))
(note-void (base/rm :list (base/ls)))

(note-md "## 1.8 - Options and Resources")

(note base/pi)
(note (base/round 'pi 5))
(note (base/options :digits 4))
(note (r 'pi))

(note ($ (base/options) 'width))
(note (base/options :width 60))
(note (base/getOption "width"))

(note (g/par :ask false))

(note-md "Revert options")
(note-void (base/options :width 120 :digits 8))

(note-md "### Excercise 1.1 - Normal percentiles")

(note (stats/qnorm 0.95))
(note (stats/qnorm [0.25 0.5 0.75]))

(note-md "### Excercise 1.2 - Chi-square density curve")

(note-void (plot->file (str target-path "/ex12.png") #(g/curve '(dchisq x :df 1))))
(note-hiccup [:image {:src "ex12.png"}])

(note-md "### Excercise 1.3 - Gamma densities")

(note-void (plot->file (str target-path "/ex13.png") (fn []
                                                       (g/curve '(dgamma x :shape 1 :rate 1) :from 0 :to 10)
                                                       (g/curve '(dgamma x :shape 2 :rate 1) :add true)
                                                       (g/curve '(dgamma x :shape 3 :rate 1) :add true))))
(note-hiccup [:image {:src "ex13.png"}])

(note-md "### Excercise 1.4 - Binomial probabilities")

(note (defn binomial [p n k]
        (* (first (r->clj (base/choose n k)))
           (Math/pow p k)
           (Math/pow (- 1.0 p) (- n k)))))

(note (map (partial binomial 1/3 12) (range 13)))

(note (r->clj (stats/dbinom (range 13) 12 1/3)))

(note-md "### Excercise 1.5 - Binomial CDF")

(note (-> (colon 0 12)
          (stats/dbinom 12 1/3)
          base/cumsum))

(note (stats/pbinom (colon 0 12) 12 1/3))

(note (r- 1.0 (stats/pbinom 7 12 1/3)))

(note-md "### Excercise 1.6 - Presidents’ heights")

(note-void (plot->file (str target-path "/ex16.png") #(g/plot opponent winner)))
(note-hiccup [:image {:src "ex16.png"}])

(note-md "### Excercise 1.7 - Simulated \"horsekicks\" data")

(note-void (def poisson-1e3 (stats/rpois 1000 :lambda 0.61)))
(note-void (def poisson-1e4 (stats/rpois 10000 :lambda 0.61)))

(note-md "Frequencies, means and variances")

(note (base/table poisson-1e3))
(note (base/table poisson-1e4))
(note (base/mean poisson-1e3))
(note (base/mean poisson-1e4))
(note (stats/var poisson-1e3))
(note (stats/var poisson-1e4))

(note-md "Density comparison")

(note (let [t1 (base/as-double (rdiv (base/table poisson-1e3) 1000))
            t2 (base/as-double (rdiv (base/table poisson-1e4) 10000))
            t (stats/dpois (range 6) :lambda 0.61)]
        {:theoretical t :simulated-1e3 t1 :simulated-1e4 t2}))

(note-md "### Excercise 1.8 - `horsekicks`, continued")

(note (def simulated-cdf (-> poisson-1e4
                             base/table
                             (rdiv 10000)
                             base/cumsum
                             (bra (colon 0 5)))))
(note (def poisson-cdf (stats/ppois (colon 0 4) 0.61)))

(note (base/matrix (base/c simulated-cdf poisson-cdf) :ncol 2))

(note-md "### Excercise 1.9 - Custom standard deviation function")

(note (def sd-n (r ['function '[x] ['sqrt [var-n 'x]]])))
(note (sd-n temps))

(note-md "### Excercise 1.10 - Euclidean norm function")

(note (def norm (r '(function [x] (sum (* x x))))))
(note (norm [0 0 0 1]))
(note (norm [2 5 2 4]))

(note-md "### Excercise 1.11 - Numerical integration")

(note (def-r fx (r ['function '[x] [(symbol "/")
                                    '(exp (* -1 (* x x)))
                                    '(+ 1 (* x x))]])))

(note-void (plot->file (str target-path "/ex111.png") #(g/curve 'fx :from 0 :to 10)))
(note-hiccup [:image {:src "ex111.png"}])

;; should be ##Inf
(note (stats/integrate 'fx :lower 0 :upper "INF"))


(note-md "### Excercise 1.12 - Bivariate normal")

(note (def x (-> (stats/rnorm 20)
                 (base/matrix 10 2))))

(note (base/apply x :MARGIN 1 :FUN norm))

(note-md "### Excercise 1.13 - `lunatics` data")

(note (base/summary lunatics1))

(note-md "### Excercise 1.14 - Tearing factor of paper")

(note (let [input [[35.0 112 119 117 113]
                   [49.5 108 99 112 118]
                   [70.0 120 106 102 109]
                   [99.0 110 101 99 104]
                   [140.0 100 102 96 101]]
            data (mapcat (fn [[p & tf]]
                           (map vector (repeat 4 p) tf)) input)]
        (base/data-frame :pressure (map first data)
                         :tearfactor (map second data))))

(note-md "### Excercise 1.15 - Vectorized operations")

(note (let [x (colon 1 8)
            n (colon 1 2)]
        (r+ x n)))

(note (let [x (colon 1 8)
            n (colon 1 3)]
        (r+ x n)))

(note/render-this-ns!)

#_(r/discard-all-sessions)

