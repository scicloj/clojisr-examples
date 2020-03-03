(ns clojisr-examples.r-by-example
  (:require [notespace.v2.note :as note
             :refer [note note-void note-md note-as-md note-hiccup note-as-hiccup]]
            [clojisr.v1.r :as r]))

(note/defkind note-def :def {:render-src?    true
                             :value-renderer (comp notespace.v2.view/value->hiccup var-get)})

(def target-path (notespace.v2.note/ns->out-dir *ns*))

(defmacro plot
  [fname & fs]
  (let [p (str target-path fname)]
    `(note-as-hiccup
      (plot->file ~p (fn [] ~@fs))
      [:image {:src ~fname}])))

(note-md "# [R by Example](https://www.springer.com/gp/book/9781461413646) by Jim Albert and Maria Rizzo - read-along")

(note-md "Code from the book ported to `clojisr` library. Read the book and run a code in Clojure.")
(note-md "[Datasets](http://personal.bgsu.edu/~mrizzo/Rx/Rx-data/)")

(note-md :Setup "## Setup")

(note-md "Imports from `clojisr` library.")

(note-void (require '[clojisr.v1.r :as r :refer [r r->clj clj->r r+ colon bra bra<-]]
                    '[clojisr.v1.require :refer [require-r]]
                    '[clojisr.v1.applications.plotting :refer [plot->file]]
                    '[tech.ml.dataset :as dataset]
                    '[notespace.v2.note :refer [check]]))

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

(note-md "Options")
(note-void (base/options :width 120 :digits 7))

(note-md "Seed")
(note (base/set-seed 11228899))

(note-md :Chapter-1---Introduction "# Chapter 1 - Introduction")
(note-md "## 1.1 - Getting Started")
(note-md "### 1.1.1 - Preliminaries")

(note-md "Various ways to call R backend.")

(note (let [x [109.0 65.0 22.0 3.0 1.0]
            x1 (r->clj (r [109.0 65 22 3 1]))
            x2 (r->clj (r "c(109, 65, 22, 3, 1)"))
            x3 (r->clj (r '[c 109.0 65.0 22.0 3.0 1.0]))
            x4 (r->clj (base/c 109.0 65.0 22.0 3.0 1.0))
            x5 (r->clj (r x))]
        (check = x1 x2 x3 x4 x5 x)))

(note-md "Assigment to a var on R side. Three variants. Be careful, vector of integers stay as integers buy c() of integers is converted to doubles.")

(note (do (r '(= x1 (c 109 65 22 3 1)))
          (r '(<- x2 (c 109 65 22 3 1)))
          (<- 'x3 [109.0 65.0 22.0 3.0 1.0])
          (check = (r->clj (r 'x1)) (r->clj (r 'x2)) (r->clj (r 'x3)))))

(note-md "We can compare object using `identical`.")

(note (do (r '(= x1 (c 109 65 22 3 1)))
          (<- 'x3 [109.0 65.0 22.0 3.0 1.0])
          (check (comp first r->clj base/identical) 'x1 'x3)))

(note-md "We will use `def-r` helper to define the same thing in Clojure and R.")

(note-void (def-r x [109 65 22 3 1]))
(note (check = x (r->clj (r 'x))))

(note-md "Below `y` on Clojure and R sides represent the same R object. `stats/rpois` is R function.")

(note (def-r y (stats/rpois 200 :lambda 0.61)))
(note (check =
             (take 10 (r->clj y))
             (take 10 (r->clj (r 'y)))))

(note-md "Usually it's enough to have variable only on Clojure side.")

(note-md "### 1.1.2 - Basic operations")

(note-md "#### Example 1.1 - Temperature data")

(note-void (def temps [51.9 51.8 51.9 53]))

(note (r ['- temps 32]))
(note (r ['* 5/9 ['- temps 32]]))

(note-md "We can define helper functions for primitive binary operations used above")

(note-void (def r- (r "`-`"))
           (def r* (r "`*`")))

(note (r- temps 32))
(note (r* 5/9 (r- temps 32)))

(note-void (def CT [48 48.2 48 48.7]))

(note (r- temps CT))

(note-md "#### Example 1.2 - President's heights")

(note-void (def winner [185 182 182 188 188 188 185 185 177 182 182 193 183 179 179 175])
           (def opponent [175 193 185 187 188 173 180 177 183 185 180 180 182 178 178 173]))

(note (let [l1 (first (r->clj (base/length winner)))
            l2 (count winner)]
        (check = l1 l2)))

(note-def (def year (base/seq :from 2008 :to 1948 :by -4)))

(note-md "or")

(note-def (def year (base/seq 2008 1948 -4)))

(note-md "Modifying data")

(note-def (def winner-var1 (-> winner
                               (bra<- 4 189)
                               (bra<- 5 189))))

(note-def (def winner-var2 (bra<- winner (colon 4 5) 189)))

(note (check = (r->clj winner-var1) (r->clj winner-var2)))

(note-void (def winner winner-var1))

(note (base/mean winner))
(note (base/mean opponent))

(note-def (def difference (r- winner opponent)))

(note-md "We have to explicitly provide column names.")

(note (base/data-frame :year year :winner winner :opponent opponent :difference difference))

(note-def (def taller-won (r ['> winner opponent])))

(note (base/table taller-won))

(note-void (def rdiv (r "`/`")))
(note (-> taller-won
          (base/table)
          (rdiv 16)
          (r* 100)))

(note-void (plot->file (str target-path "ch1ex2.png") #(g/barplot (base/rev difference)
                                                                  :xlab "Election years 1948 to 2008",
                                                                  :ylab "Height difference in cm")))
(note-hiccup [:image {:src "ch1ex2.png"}])

(note-void (plot->file (str target-path "ch1ex2b.png") #(g/plot winner opponent
                                                                :xlab "winner" :ylab "opponent")))
(note-hiccup [:image {:src "ch1ex2b.png"}])

(note-md "#### Example 1.3 - horsekicks")

(note-void (def k [0 1 2 3 4])
           (def x [109 65 22 3 1]))

(note-void (plot->file (str target-path "ch1ex3.png") #(g/barplot x :names.arg k)))
(note-hiccup [:image {:src "ch1ex3.png"}])

(note-void (def rpow (r "`^`")))

(note-def (def p (rdiv x (base/sum x))))
(note-def (def rr (base/sum (r* p k))))
(note-def (def v (-> (r- k rr)
                     (rpow 2)
                     (r* x)
                     (base/sum)
                     (rdiv 199))))

(note-def (def f1 (-> (r- rr)
                      (base/exp)
                      (r* (rpow rr k))
                      (rdiv (base/factorial k))
                      (r->clj))))

(note-def (def f2 (r->clj (stats/dpois k rr))))

(note-void (def f f2))

(note-md "Expected counts")
(note (r->clj (base/floor (r* 200 f))))
(note-md "Observed counts")
(note x)

(note (base/cbind k p f))

(note-md "### 1.1.3 - R Scripts")

(note-md "#### Example 1.4 - Simulated horsekick data")

(note-def (def y (stats/rpois 200 :lambda 0.61)))

(note-md "Table of sample frequencies")
(note-def (def kicks (base/table y)))

(note-md "Sample proportions")
(note (rdiv kicks 200))

(note-def (def theoretical (-> kicks
                               r->clj
                               count
                               range
                               (stats/dpois :lambda 0.61))))
(note-def (def sample (rdiv kicks 200)))
(note (base/cbind :theoretical  theoretical :sample sample))

(note (base/mean y))
(note (stats/var y))

(note-md "### 1.1.4 - The R Help System")

(note-md "Unfortunately `help` or `example` commands do not work")

(note-md "## 1.2 - Functions")

(note-md "Two ways to define functions.")

(note-md "R string")
(note-def (def f1 (r "function(...) {1.0}")))
(note-md "Clojure style")
(note-def (def f2 (r '(function [...] 1.0))))

(note (check = (r->clj (f1)) (r->clj (f2))))

(note-md "#### Example 1.5 - function definition")

(note-void (def var-n (r '(function [x]
                                    (= v (var x))
                                    (= n (NROW x))
                                    (/ (* (- n 1) v) n)))))

(note (stats/var temps))
(note (var-n temps))

(note-md "#### Example 1.6 - functions as arguments")

(note-void (def f (r "function(x, a=1, b=1) x^(a-1) * (1-x)^(b-1)")))
(note (let [x (base/seq 0 1 0.2)]
        (f x :a 2 :b 2)))

(note (stats/integrate f :lower 0 :upper 1 :a 2 :b 2))

(note (base/beta 2 2))

(note-md "#### Example 1.7 - graphing a function using `curve`")

(note-void (plot->file (str target-path "ch1ex7.png") #(g/curve '(* x (- 1 x))
                                                                 :from 0 :to 1 :ylab "f(x)")))
(note-hiccup [:image {:src "ch1ex7.png"}])


(note-md "## 1.3 - Vectors and Matrices")

(note-md "#### Example 1.8 - Class mobility")

(note-void (def probs [0.45 0.05 0.01 0.48 0.70 0.50 0.07 0.25 0.49]))
(note-def (def P (base/matrix probs :nrow 3 :ncol 3)))
(note-md "Currently matrix is flat array in Clojure")
(note (r->clj P))

(note-def (def P (let [names ["lower" "middle" "upper"]]
                   (-> P
                       (base/rownames<- names)
                       (base/colnames<- names)))))

(note (base/rowSums P))
(note (base/apply P :MARGIN 1 :FUN base/sum))

(note-def (def P2 (base/%*% P P)))

(note (bra P2 1 3))
(note (bra P2 1 (r/empty-symbol)))

(note (let [P4 (base/%*% P2 P2)
            P8 (base/%*% P4 P4)]
        P8))


(note-def (def Q (base/matrix [0.45 0.48 0.07
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

(note-void (plot->file (str target-path "ch1ex9.png") #(g/hist ($ USArrests 'Assault)
                                                               :xlab "Assault"
                                                               :main "Histogram of Assault")))
(note-hiccup [:image {:src "ch1ex9.png"}])

(note (require-r '[MASS]))

(note-void (plot->file (str target-path "ch1ex9b.png") #(r.MASS/truehist ($ USArrests 'Assault)
                                                                         :xlab "Assault")))
(note-hiccup [:image {:src "ch1ex9b.png"}])

(note-void (plot->file (str target-path "ch1ex9c.png") #(g/hist ($ USArrests 'Assault)
                                                                :prob true :breaks "scott"
                                                                :xlab "Assault"
                                                                :main "Histogram of Assault")))
(note-hiccup [:image {:src "ch1ex9c.png"}])

(note-md "Attach creates symbols on the R side, use Clojure symbols (ex. 'Murder) to access them.")
(note (base/attach USArrests))

(note-def (def murder-pct (r '(/ (* 100 Murder)
                                 (+ (+ Murder Assault) Rape)))))
(note (u/head murder-pct))

#_(note-md "problems with `with`")
#_(note (base/with USArrests :expr '(= murder-pct (/ (* 100 Murder)
                                                     (+ (+ Murder Assault) Rape)))))

(note-void (plot->file (str target-path "ch1ex9d.png") #(g/plot 'UrbanPop 'Murder)))
(note-hiccup [:image {:src "ch1ex9d.png"}])

(note-void (plot->file (str target-path "ch1ex9e.png") #(g/pairs USArrests)))
(note-hiccup [:image {:src "ch1ex9e.png"}])

(note (stats/cor 'UrbanPop 'Murder))
(note (stats/cor USArrests))

(note-md "## 1.5 - Importing Data")

(note-md "### 1.5.1 - Entering data manually")

(note-md "#### Example 1.11 - Data from a textbook")

(note-def (def y (let [y1 [22 26]
                       y2 [28 24 29]
                       y3 [29 32 28]
                       y4 [23 24]]
                   (concat y1 y2 y3 y4))))

(note-def (def model (base/c (base/rep "A" 2)
                             (base/rep "B" 3)
                             (base/rep "C" 3)
                             (base/rep "D" 2))))

(note-def (def mileages (base/data-frame :y y :model model)))

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
(note-def (def prop (rdiv (base/table pidigits) 5000)))
(note (Math/sqrt (/ (* 0.1 0.9) 5000)))
(note (base/sqrt (rdiv (r* 0.1 0.9) 5000)))
(note-def (def se-hat (base/sqrt (rdiv (r* prop (r- 1.0 prop)) 5000))))
(note (-> (base/rbind :prop prop :se.hat se-hat
                      (r- prop (r* 2.0 se-hat))
                      (r+ prop (r* 2.0 se-hat)))
          (base/round 4)))

(note-void (plot->file (str target-path "ch1ex13.png") (fn []
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
(note (base/ls))
(note-void (base/rm 'v))
(note (base/ls))
(note-void (base/rm :list ["f" "r"]))
(note (base/ls))
(note-void (base/rm :list (base/ls)))
(note (base/ls))

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

(note-md "## Exercises")
(note-md "### Exercise 1.1 - Normal percentiles")

(note (stats/qnorm 0.95))
(note (stats/qnorm [0.25 0.5 0.75]))

(note-md "### Exercise 1.2 - Chi-square density curve")

(note-void (plot->file (str target-path "ex12.png") #(g/curve '(dchisq x :df 1)
                                                              :ylab "dchisq(x,1)")))
(note-hiccup [:image {:src "ex12.png"}])

(note-md "### Exercise 1.3 - Gamma densities")

(note-void (plot->file (str target-path "ex13.png") (fn []
                                                      (g/curve '(dgamma x :shape 1 :rate 1) :from 0 :to 10
                                                               :ylab "dgamma(shape=[1:3], rate=1)")
                                                      (g/curve '(dgamma x :shape 2 :rate 1) :add true)
                                                      (g/curve '(dgamma x :shape 3 :rate 1) :add true))))
(note-hiccup [:image {:src "ex13.png"}])

(note-md "### Exercise 1.4 - Binomial probabilities")

(note-void (defn binomial [p n k]
             (* (first (r->clj (base/choose n k)))
                (Math/pow p k)
                (Math/pow (- 1.0 p) (- n k)))))

(note (map (partial binomial 1/3 12) (range 13)))

(note (r->clj (stats/dbinom (range 13) 12 1/3)))

(note-md "### Exercise 1.5 - Binomial CDF")

(note (-> (colon 0 12)
          (stats/dbinom 12 1/3)
          base/cumsum))

(note (stats/pbinom (colon 0 12) 12 1/3))

(note (r- 1.0 (stats/pbinom 7 12 1/3)))

(note-md "### Exercise 1.6 - Presidents' heights")

(note-void (plot->file (str target-path "ex16.png") #(g/plot opponent winner
                                                             :xlab "opponent"
                                                             :ylab "winner")))
(note-hiccup [:image {:src "ex16.png"}])

(note-md "### Exercise 1.7 - Simulated \"horsekicks\" data")

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

(note (let [sel (colon 0 5)
            t1 (bra (base/as-double (rdiv (base/table poisson-1e3) 1000)) sel)
            t2 (bra (base/as-double (rdiv (base/table poisson-1e4) 10000)) sel)
            t (stats/dpois (range 5) :lambda 0.61)]
        (base/data-frame :theoretical t :simulated1e3 t1 :simulated1e4 t2)))

(note-md "### Exercise 1.8 - `horsekicks`, continued")

(note-def (def simulated-cdf (-> poisson-1e4
                                 base/table
                                 (rdiv 10000)
                                 base/cumsum
                                 (bra (colon 0 5)))))
(note-def (def poisson-cdf (stats/ppois (colon 0 4) 0.61)))

(note (base/matrix (base/c simulated-cdf poisson-cdf) :ncol 2))

(note-md "### Exercise 1.9 - Custom standard deviation function")

(note-void (def sd-n (r ['function '[x] ['sqrt [var-n 'x]]])))
(note (sd-n temps))

(note-md "### Exercise 1.10 - Euclidean norm function")

(note-void (def norm (r '(function [x] (sum (* x x))))))
(note (norm [0 0 0 1]))
(note (norm [2 5 2 4]))

(note-md "### Exercise 1.11 - Numerical integration")

(note (def-r fx (r ['function '[x] [(symbol "/")
                                    '(exp (* -1 (* x x)))
                                    '(+ 1 (* x x))]])))

(note-void (plot->file (str target-path "ex111.png") #(g/curve 'fx :from 0 :to 10)))
(note-hiccup [:image {:src "ex111.png"}])

;; should be ##Inf
(note (stats/integrate 'fx :lower 0 :upper "INF"))


(note-md "### Exercise 1.12 - Bivariate normal")

(note-def (def x (-> (stats/rnorm 20)
                     (base/matrix 10 2))))

(note (base/apply x :MARGIN 1 :FUN norm))

(note-md "### Exercise 1.13 - `lunatics` data")

(note (base/summary lunatics1))

(note-md "### Exercise 1.14 - Tearing factor of paper")

(note (let [input [[35.0 112 119 117 113]
                   [49.5 108 99 112 118]
                   [70.0 120 106 102 109]
                   [99.0 110 101 99 104]
                   [140.0 100 102 96 101]]
            data (mapcat (fn [[p & tf]]
                           (map vector (repeat 4 p) tf)) input)]
        (base/data-frame :pressure (map first data)
                         :tearfactor (map second data))))

(note-md "### Exercise 1.15 - Vectorized operations")

(note (let [x (colon 1 8)
            n (colon 1 2)]
        (r+ x n)))

(note (let [x (colon 1 8)
            n (colon 1 3)]
        (r+ x n)))

(note-md :Chapter-2---Quantitative-Data "# Chapter 2 - Quantitative Data")

(note-md "## 2.2 -  Bivariate Data: Two Quantitative Variables")
(note-md "### 2.2.1 - Exploring the data")
(note-md "#### Example 2.1 - `mammals`")


(note (u/head r.MASS/mammals))
(note (base/is-matrix 'mammals))
(note (base/is-data-frame 'mammals))

(note (base/summary 'mammals))

(note-void (plot->file (str target-path "ch2ex21.png") #(g/boxplot r.MASS/mammals)))
(note-hiccup [:image {:src "ch2ex21.png"}])

(note-void (plot->file (str target-path "ch2ex21b.png") #(g/plot r.MASS/mammals)))
(note-hiccup [:image {:src "ch2ex21b.png"}])

(note-void (plot->file (str target-path "ch2ex21c.png") #(g/plot (base/log ($ 'mammals 'body))
                                                                  (base/log ($ 'mammals 'brain))
                                                                  :xlab "log(body)"
                                                                  :ylab "log(brain)")))
(note-hiccup [:image {:src "ch2ex21c.png"}])

(note (base/summary (base/log 'mammals)))

(note-void (plot->file (str target-path "ch2ex21d.png") #(g/boxplot (base/log r.MASS/mammals)
                                                                  :names ["log(body)" "log(brain)"])))
(note-hiccup [:image {:src "ch2ex21d.png"}])

(note-md "### 2.2.2 - Correlation and regression line")

(note (stats/cor (base/log 'mammals)))
(note (stats/cor (base/log ($ 'mammals 'body))
                 (base/log ($ 'mammals 'brain))))

(note-void (plot->file (str target-path "ch2ex21e.png") #(let [x (<- 'x (base/log ($ 'mammals 'body)))
                                                             y (<- 'y (base/log ($ 'mammals 'brain)))]
                                                         (g/plot x y
                                                                 :xlab "log(body)"
                                                                 :ylab "log(brain)")
                                                         (g/abline (stats/lm (r "y ~ x"))))))
(note-hiccup [:image {:src "ch2ex21e.png"}])

(note-md "### 2.2.3 - Analysis of bivariate data by group")
(note-md "#### Example 2.2 - IQ of twins separated near birth")

(note-void (def twins (u/read-table "data/twinIQ.txt" :header true)))
(note (u/head twins))
(note (base/summary twins))

(note-void (plot->file (str target-path "ch2ex22.png") #(g/boxplot (r "Foster - Biological ~ Social") twins)))
(note-hiccup [:image {:src "ch2ex22.png"}])

(note-def (def status (base/as-integer ($ twins 'Social))))

(note-void (plot->file (str target-path "ch2ex22b.png") (fn []
                                                        (g/plot (r "Foster ~ Biological")
                                                                :data twins
                                                                :pch status :col status)
                                                        (g/legend "topleft" ["high" "low" "middle"]
                                                                  :pch (colon 1 3)
                                                                  :col (colon 1 3)
                                                                  :inset 0.02)
                                                        (g/abline 0 1))))
(note-hiccup [:image {:src "ch2ex22b.png"}])

(note-md "### 2.2.4 - Conditional plots")

(note-void (plot->file (str target-path "ch2ex22c.png") #(g/coplot (r "Foster ~ Biological | Social") :data twins)))
(note-hiccup [:image {:src "ch2ex22c.png"}])

(note-void (require-r '[lattice]))

(note-void (plot->file (str target-path "ch2ex22d.png") (r.lattice/xyplot (r "Foster ~ Biological | Social")
                                                                        :data twins :pch 20 :col 1)))
(note-hiccup [:image {:src "ch2ex22d.png"}])

(note-md "## 2.3 - Multivariate Data: Several Quantitative Variables")
(note-md "#### Example 2.3 - Brain size and intelligence")

(note-void (def brain (u/read-table "data/brainsize.txt" :header true)))
(note (base/summary brain))

(note (base/mean ($ brain 'Weight)))
(note (base/mean ($ brain 'Weight) :na.rm true))
(note (base/by :data (bra brain (r/empty-symbol) -1)
               :INDICES ($ brain 'Gender)
               :FUN base/colMeans
               :na.rm true))

(note-void (base/attach brain))

(note-void (plot->file (str target-path "ch2ex23.png") (fn [] (let [gender (base/as-integer 'Gender)]
                                                             (g/plot 'Weight 'MRI_Count
                                                                     :pch gender :col gender)
                                                             (g/legend "topleft" ["Female" "Male"]
                                                                       :pch (colon 1 2)
                                                                       :col (colon 1 2)
                                                                       :inset 0.02)))))
(note-hiccup [:image {:src "ch2ex23.png"}])

(note-void (plot->file (str target-path "ch2ex23b.png") #(g/pairs (bra brain (r/empty-symbol) (colon 2 7)))))
(note-hiccup [:image {:src "ch2ex23b.png"}])

(note (-> brain
          (bra (r/empty-symbol) (colon 2 7))
          (stats/cor)
          (base/round 2)))

(note (-> brain
          (bra (r/empty-symbol) (colon 2 7))
          (stats/cor :use "pairwise.complete.obs")
          (base/round 2)))

(note-def (def mri (rdiv 'MRI_Count 'Weight)))

(note (stats/cor 'FSIQ mri :use "pairwise.complete.obs"))

(note (stats/cor-test 'FSIQ 'MRI_Count))
(note (-> (stats/cor-test 'FSIQ mri)
          r->clj
          :p.value))

(note-md "### 2.3.5 - Identifying missing values")

(note (base/which (base/is-na brain) :arr.ind true))
(note (bra brain [2 21] (r/empty-symbol)))

(note (let [mw (base/mean ($ brain 'Weight) :na.rm true)
            mh (base/mean ($ brain 'Height) :na.rm true)]
        (-> brain
            (bra<- 2 5 mw)
            (bra<- 21 (colon 5 6) (base/c mw mh))
            (bra [2 21] (r/empty-symbol)))))

(note-md "## 2.4 - Time Series Data")
(note-md "#### Example 2.4 - New Haven temperatures")

(note nhtemp)

(note-void (plot->file (str target-path "ch2ex24.png") #(g/plot nhtemp :ylab "temperatures")))
(note-hiccup [:image {:src "ch2ex24.png"}])

(note-void (plot->file (str target-path "ch2ex24b.png") (fn []
                                                        (g/plot nhtemp :ylab "Mean annual temperatures")
                                                        (g/abline :h (base/mean nhtemp))
                                                        (g/lines (stats/lowess nhtemp)))))
(note-hiccup [:image {:src "ch2ex24b.png"}])

(note-def (def d (base/diff nhtemp)))

(note-void (plot->file (str target-path "ch2ex24c.png") (fn []
                                                        (g/plot d :ylab "First differences of mean annual temperatures")
                                                        (g/abline :h 0 :lty 3)
                                                        (g/lines (stats/lowess d)))))
(note-hiccup [:image {:src "ch2ex24c.png"}])

(note-md "## 2.5 - Integer Data: Draft Lottery")

(note-md "#### Example 2.5 - The 1970 Draft Lottery Data")

(note-void (def draftnums (u/read-table "data/draft-lottery.txt" :header true)))
(note (base/names draftnums))
(note (bra ($ draftnums 'Jan) 15))
(note-def (def months (bra draftnums (colon 2 13))))
(note-def (def medians (base/sapply months stats/median :na.rm true)))

(note-void (plot->file (str target-path "ch2ex25.png") #(g/plot medians :type "b" :xlab "month number" :ylab "medians")))
(note-hiccup [:image {:src "ch2ex25.png"}])

(note-void (plot->file (str target-path "ch2ex25b.png") #(g/boxplot months)))
(note-hiccup [:image {:src "ch2ex25b.png"}])

(note-md "## 2.6 - Sample Means and the Central Limit Theorem")
(note-md "#### Example 2.6 - Sample means")

(note (base/colMeans randu))
(note (stats/var randu))
(note (-> randu stats/var base/diag))

(note-void (plot->file (str target-path "ch2ex26.jpg") (r.lattice/cloud (r "z ~ x + y") :data randu) :quality 85))
(note-hiccup [:image {:src "ch2ex26.jpg"}])

(note-void (def means (base/apply randu :MARGIN 1 :FUN base/mean)))
(note-void (def means (base/rowMeans randu)))

(note-void (plot->file (str target-path "ch2ex26b.png") #(g/hist means
                                                                 :xlab "means"
                                                                 :main "Histogram of means")))
(note-hiccup [:image {:src "ch2ex26b.png"}])

(note-void (plot->file (str target-path "ch2ex26c.png") #(g/hist means :prob true
                                                                 :xlab "means"
                                                                 :main "Histogram of means")))
(note-hiccup [:image {:src "ch2ex26c.png"}])

(note-void (plot->file (str target-path "ch2ex26d.png") #(g/plot (stats/density means)
                                                                 :main "Density of means")))
(note-hiccup [:image {:src "ch2ex26d.png"}])

(note-void (plot->file (str target-path "ch2ex26e.png") (fn []
                                                          (r.MASS/truehist means :xlab "means")
                                                          (g/curve '(dnorm x 1/2 :sd (sqrt 1/36)) :add true))))
(note-hiccup [:image {:src "ch2ex26e.png"}])

(note-void (plot->file (str target-path "ch2ex26f.png") (fn []
                                                          (stats/qqnorm means)
                                                          (stats/qqline means))))
(note-hiccup [:image {:src "ch2ex26f.png"}])

(note-md "## 2.7 - Special Topics")
(note-md "### 2.7.1 - Adding a new variable")
(note-md "#### Example 2.7 - `mammals`, cont.")

(note-def (def m (stats/median ($ r.MASS/mammals 'body))))
(note-void (def mammals ($<- r.MASS/mammals 'size (base/ifelse (r/r>= ($ r.MASS/mammals 'body) m)
                                                               "large"
                                                               "small"))))
(note (u/head mammals))
(note (base/subset mammals '(== size "large")))

(note-md "### 2.7.2 - Which observation is the maximum?")

(note (base/which (r/r> ($ mammals 'body) 2000)))
(note (bra mammals [19 33] (r/empty-symbol)))
(note (base/max ($ mammals 'body)))
(note (base/which-max ($ mammals 'body)))
(note (bra mammals 33 (r/empty-symbol)))
(note (base/which-min ($ mammals 'body)))
(note (bra mammals 14 (r/empty-symbol)))

(note-md "### 2.7.3 - Sorting a data frame")
(note-md "#### Example 2.8 - Sorting mammals")

(note-def (def x (bra mammals (colon 1 5) (r/empty-symbol))))
(note-def (def o (base/order ($ x 'body))))
(note (bra x o (r/empty-symbol)))

(note (let [o (base/order ($ mammals 'body))
            sorted-data (bra mammals o (r/empty-symbol))]
        (u/tail sorted-data 3)))

(note-md "### 2.7.4 - Distances between points")
(note-md "#### Example 2.9 - Distances between points")

(note-def (def x (bra r.MASS/mammals (colon 1 5) (r/empty-symbol))))
(note (stats/dist x))
(note (-> x stats/dist base/as-matrix))

(note-def (def y (base/log (bra r.MASS/mammals
                                ["Grey wolf", "Cow", "Human"]
                                (r/empty-symbol)))))

(note-void (plot->file (str target-path "ch2ex29.png") (fn []
                                                         (g/plot (base/log ($ r.MASS/mammals 'body))
                                                                 (base/log ($ r.MASS/mammals 'brain))
                                                                 :xlab "log(body)" :ylab "log(brain)")
                                                         (g/polygon y)
                                                         (g/text y (base/rownames y) :adj [1.0 0.5]))))
(note-hiccup [:image {:src "ch2ex29.png"}])

(note (stats/dist y))

(note-md "### 2.7.5 - Quick look at cluster analysis")
(note-md "#### Example 2.10 - Cluster analysis of distances")

(note-def (def h (-> r.MASS/mammals base/log stats/dist (stats/hclust :method "complete"))))

(note-def (def big (base/subset r.MASS/mammals :subset '(> body (median body)))))

(note-def (def h2 (-> big base/log stats/dist (stats/hclust :method "complete"))))

(note-void (plot->file (str target-path "ch2ex210.png") #(g/plot h2 :xlab "hclust, method complete" :sub "")))
(note-hiccup [:image {:src "ch2ex210.png"}])

(note (u/head ($ h 'merge)))
(note (bra (base/rownames r.MASS/mammals) [22 28]))
(note (bra (base/log r.MASS/mammals) [22 28] (r/empty-symbol)))

(note-md "## Exercises")

(note-md "### Exercise 2.1 - `chickwts` data")

(note-void (plot->file (str target-path "ex21.png") #(g/boxplot (r "weight ~ feed") chickwts)))
(note-hiccup [:image {:src "ex21.png"}])

(note-md "### Exercise 2.2 - `iris` data")

(note (base/by :data (bra iris (r/empty-symbol) [1 2 3 4])
               :INDICES ($ iris 'Species)
               :FUN base/colMeans
               :na.rm true))

(note-md "### Exercise 2.3 - `mtcars` data")

(note-void (plot->file (str target-path "ex23.png") #(g/boxplot (bra mtcars [-3 -4]))))
(note-void (plot->file (str target-path "ex23b.png") #(g/boxplot (bra mtcars [3 4]))))
(note-hiccup [:div
              [:image {:src "ex23.png"}]
              [:image {:src "ex23b.png"}]])

(note-void (plot->file (str target-path "ex23c.png") #(g/pairs mtcars)))
(note-hiccup [:image {:src "ex23c.png"}])

(note-md "### Exercise 2.4 - `mammals` data")

(note-void (def mammals ($<- r.MASS/mammals 'r (rdiv ($ r.MASS/mammals 'brain)
                                                     ($ r.MASS/mammals 'body)))))

(note-void (def sorted-mammals (let [o (base/order ($ mammals 'r))]
                                 (bra mammals o (r/empty-symbol)))))

(note (u/head sorted-mammals 5))
(note (u/tail sorted-mammals 5))

(note-md "### Exercise 2.5 - `mammals` data, continued")

(note-void (plot->file (str target-path "ex25.png") #(g/plot ($ mammals 'r)
                                                             (base/log ($ mammals 'body))
                                                             :xlab "brain/body ratio"
                                                             :ylab "log(body)")))
(note-hiccup [:image {:src "ex25.png"}])

(note-md "### Exercise 2.6 - `LakeHuron` data")

(note-void (plot->file (str target-path "ex26.png") (fn []
                                                      (g/plot LakeHuron :ylab "level (in feet)")
                                                      (g/abline :h (base/mean LakeHuron) :lty 3)
                                                      (g/lines (stats/lowess LakeHuron)))))
(note-hiccup [:image {:src "ex26.png"}])

(note-void (def d (base/diff LakeHuron)))

(note-void (plot->file (str target-path "ex26b.png") (fn []
                                                       (g/plot d :ylab "level differences (in feet)")
                                                       (g/abline :h 0 :lty 3)
                                                       (g/lines (stats/lowess d)))))
(note-hiccup [:image {:src "ex26b.png"}])

(note-md "### Exercise 2.7 - Central Limit Theorem with simulated data")

(note-void (def runif-data (-> (stats/runif 1200)
                               (base/matrix 400)
                               (base/colnames<- ["x" "y" "z"])
                               (base/as-data-frame))))

(note (base/colMeans runif-data))
(note (stats/var runif-data))
(note (-> runif-data stats/var base/diag))

(note-void (plot->file (str target-path "ex27.jpg") (r.lattice/cloud (r "z ~ x + y") :data runif-data) :quality 85))
(note-hiccup [:image {:src "ex27.jpg"}])

(note-void (def means (base/rowMeans runif-data)))

(note-void (plot->file (str target-path "ex27b.png") #(g/hist means :prob true
                                                              :xlab "means"
                                                              :main "Histogram of means")))
(note-hiccup [:image {:src "ex27b.png"}])

(note-void (plot->file (str target-path "ex27c.png") #(g/plot (stats/density means)
                                                              :main "Density of means")))
(note-hiccup [:image {:src "ex27c.png"}])

(note-void (plot->file (str target-path "ex27d.png") (fn []
                                                       (r.MASS/truehist means
                                                                        :xlab "means"
                                                                        :main "Histogram of means")
                                                       (g/curve '(dnorm x 1/2 :sd (sqrt 1/36)) :add true))))
(note-hiccup [:image {:src "ex27d.png"}])

(note-void (plot->file (str target-path "ex27e.png") (fn []
                                                       (stats/qqnorm means)
                                                       (stats/qqline means))))
(note-hiccup [:image {:src "ex27e.png"}])

(note-md "### Exercise 2.8 - Central Limit Theorem, continued")

(note-void (def runif-data (-> (stats/runif 4000)
                               (base/matrix 400))))

(note (base/colMeans runif-data))
(note (-> runif-data stats/var base/diag))

(note-void (def means (base/rowMeans runif-data)))

(note-void (plot->file (str target-path "ex28.png") #(g/hist means :prob true
                                                             :xlab "means"
                                                             :main "Histogram of means")))
(note-hiccup [:image {:src "ex28.png"}])

(note-void (plot->file (str target-path "ex28b.png") #(g/plot (stats/density means) :main "Density of means")))
(note-hiccup [:image {:src "ex28b.png"}])

(note-void (plot->file (str target-path "ex28c.png") (fn []
                                                       (r.MASS/truehist means
                                                                        :xlab "means"
                                                                        :main "Histogram of means")
                                                       (g/curve '(dnorm x 1/2 :sd (sqrt 1/120)) :add true))))
(note-hiccup [:image {:src "ex28c.png"}])

(note-void (plot->file (str target-path "ex28d.png") (fn []
                                                       (stats/qqnorm means)
                                                       (stats/qqline means))))
(note-hiccup [:image {:src "ex28d.png"}])

(note-md "### Exercise 2.9 - 1970 Vietnam draft lottery")

(note-md "skipped")

(note-md "### Exercise 2.10 - \"Old Faithful\" histogram")

(note-void (plot->file (str target-path "ex210.png") #(g/hist ($ faithful 'waiting) :prob true
                                                              :xlab "waiting"
                                                              :main "Histogram of waiting")))
(note-hiccup [:image {:src "ex210.png"}])

(note-md "### Exercise 2.11 - \"Old Faithful\" density estimate")

(note-void (plot->file (str target-path "ex211.png") (fn []
                                                       (let [h (g/hist ($ faithful 'waiting) :prob true
                                                                       :xlab "waiting"
                                                                       :main "Histogram of waiting")]
                                                         (base/print h)
                                                         (g/lines ($ h 'mids) ($ h 'density) :add true)))))
(note-hiccup [:image {:src "ex211.png"}])

(note-md "### Exercise 2.12 - Ordering the `mammals` data by brain size")

(note-void (def sorted-mammals-by-brain
             (let [o (base/order ($ mammals 'brain))]
               (bra mammals o (r/empty-symbol)))))

(note (u/tail sorted-mammals-by-brain 5))
(note (u/head sorted-mammals-by-brain 5))

(note-md "### Exercise 2.13 - `mammals` data on original scale")

(note-def (def y (bra r.MASS/mammals
                      ["Cat", "Cow", "Human"]
                      (r/empty-symbol))))

(note-void (plot->file (str target-path "ex213.png") (fn []
                                                       (g/plot ($ r.MASS/mammals 'body)
                                                               ($ r.MASS/mammals 'brain)
                                                               :xlab "body" :ylab "brain")
                                                       (g/polygon y)
                                                       (g/text y (base/rownames y) :adj [1.0 0.5]))))
(note-hiccup [:image {:src "ex213.png"}])

(note-md "### Exercise 2.14 - `mammals` cluster analysis")

(note-void (def dist2 (let [d (base/log (stats/dist big))] (r* d d))))
(note-def (def clust (stats/hclust dist2 :method "ward.D")))

(note-void (plot->file (str target-path "ex214.png") #(g/plot clust :xlab "hclust, method ward.D" :sub "")))
(note-hiccup [:image {:src "ex214.png"}])

(note-md "### Exercise 2.15 - Identifying groups or clusters")

(note-md "*possible wrong result*")

(note-def (def g (stats/cutree h 5)))
(note (base/table g))
(note (bra r.MASS/mammals (r/r== g 5) (r/empty-symbol)))

(note-md :Chapter-3---Categorical-data "# Chapter 3 - Categorical data")
(note-md "## 3.1 - Introduction")
(note-md "### 3.1.1 - Tabulating and plotting categorical data")
(note-md "#### Example 3.1 - Flipping a coin")

(note-void (def tosses (map str "HTHHTHHTHHTTHTTTHHHT")))
(note (base/table tosses))
(note-def (def prop-tosses (rdiv (base/table tosses)
                                 (base/length tosses))))

(note-void (plot->file (str target-path "ch3ex31.png") #(g/plot prop-tosses :ylab "tosses probability")))
(note-void (plot->file (str target-path "ch3ex31b.png") #(g/barplot prop-tosses)))
(note-hiccup [:div
              [:image {:src "ch3ex31.png"}]
              [:image {:src "ch3ex31b.png"}]])

(note-md "### 3.1.2 - Character vectors and factors")
(note-md "#### Example 3.2 - Rolling a die")

(note-void (def y [1 4 3 5 4 2 4]))
(note-void (def possible-rolls [1 2 3 4 5 6]))
(note-void (def label-rolls ["one" "two" "three" "four" "five" "six"]))
(note-def (def fy (base/factor y :levels possible-rolls :labels label-rolls)))
(note (base/table fy))

(note-md "## 3.2 - Chi-square Goodness-of-Fit Test")
(note-md "#### Example 3.3 - Weldon’s dice")

(note-def (def k (colon 0 12)))
(note-def (def p (stats/dbinom k :size 12 :prob 1/3)))
(note-def (def binom (-> (base/round (r* 26306 p))
                         (base/names<- k))))
(note-def (def weldon (-> (base/c 185 1149 3265 5475 6114 5194 3067 1331 403 105 14 4 0)
                          (base/names<- k))))
(note (r->clj (base/data-frame :Binom binom :Weldon weldon :Diff (r- weldon binom))))
(note-def (def counts (-> (base/cbind binom weldon)
                          (base/colnames<- ["binom" "weldon"]))))

(note-void (plot->file (str target-path "ch3ex33.png") #(g/barplot counts :beside true)))
(note-hiccup  [:image {:src "ch3ex33.png"}])

(note-void (plot->file (str target-path "ch3ex33b.png") (fn []
                                                          (g/plot k binom :type "h" :lwd 2 :lty 1 :ylab "Count" :xlab "k")
                                                          (g/lines (r/r+ k 0.2) weldon :type "h" :lwd 2 :lty 2)
                                                          (g/legend 8 5000 :legend ["Binomial" "Weldon"]
                                                                    :lty [1 2] :lwd [2 2]))))
(note-hiccup  [:image {:src "ch3ex33b.png"}])

(note-def (def cweldon (base/c (bra weldon (colon 1 10))
                               (base/sum (bra weldon [11 12 13])))))
(note-def (def probs (base/c (bra p (colon 1 10))
                             (r- 1.0 (base/sum (bra p (colon 1 10)))))))
(note-def (def test (stats/chisq-test cweldon :p probs)))

(note-void (plot->file (str target-path "ch3ex33c.png") (fn []
                                                          (g/plot (colon 0 10)
                                                                  ($ test 'residuals)
                                                                  :xlab "k"
                                                                  :ylab "Residual")
                                                          (g/abline :h 0))))
(note-hiccup  [:image {:src "ch3ex33c.png"}])

(note-md "## 3.3 -  Relating Two Categorical Variables")
(note-md "### 3.3.1 - Introduction")
(note-md "#### Example 3.4 - The twins dataset")
(note-md "### 3.3.2 - Frequency tables and graphs")

(note-void (def twn (u/read-table "data/twins.dat.txt"
                                  :header true
                                  :sep ","
                                  :na.strings ".")))

(note (base/table ($ twn 'EDUCL)))
(note (base/table ($ twn 'EDUCH)))

(note-void (def breaks [0 12 15 16 24]))
(note-void (def labels ["High School" "Some College" "College Degree" "Graduate School"]))

(note-void (def c-educl (base/cut ($ twn 'EDUCL) :breaks breaks :labels labels)))
(note-void (def c-educh (base/cut ($ twn 'EDUCH) :breaks breaks :labels labels)))

(note (base/table c-educl))
(note (base/prop-table (base/table c-educl)))

(note-void (plot->file (str target-path "ch3ex34.png") #(-> c-educl
                                                            base/table
                                                            base/prop-table
                                                            g/barplot) :width 520))
(note-hiccup  [:image {:src "ch3ex34.png"}])

(note-void (plot->file (str target-path "ch3ex34b.png") #(g/mosaicplot (base/table c-educl)
                                                                       :main "c-educl")))
(note-hiccup  [:image {:src "ch3ex34b.png"}])

(note-md "### 3.3.3 - Contingency tables")

(note-def (def t1 (base/table c-educl c-educh)))
(note (base/diag t1))
(note (rdiv (-> t1 base/diag base/sum)
            (-> t1 base/sum)))

(note-void (plot->file (str target-path "ch3ex34c.png") #(g/plot t1 :main "T" :xlab "c-educl" :ylab "c-educh")))
(note-hiccup  [:image {:src "ch3ex34c.png"}])

(note-md "## 3.4 - Association Patterns in Contingency Tables")
(note-md "### 3.4.1 - Constructing a contingency table")

(note-void (def c-wage (base/cut ($ twn 'HRWAGEL) [0 7 13 20 150])))
(note (base/table c-wage))
(note-def (def t2 (base/table c-educl c-wage :dnn ["c.educl" "c.wage"])))
(note-def (def P (base/prop-table t2 :margin 1)))

(note-md "### 3.4.2 - Graphing patterns of association")

(note-void (plot->file (str target-path "ch3ex34d.png") #(g/barplot (base/t P)
                                                                    :ylim [0 1.3]
                                                                    :ylab "PROPOTION"
                                                                    :legend.text ($ (base/dimnames P) 'c.wage)
                                                                    :args.legend {:x "top"}) :width 520))
(note-hiccup  [:image {:src "ch3ex34d.png"}])

(note-void (plot->file (str target-path "ch3ex34e.png") #(g/barplot (base/t P)
                                                                    :beside true
                                                                    :ylab "PROPOTION"
                                                                    :legend.text ($ (base/dimnames P) 'c.wage)
                                                                    :args.legend {:x "topleft"}) :width 520))
(note-hiccup  [:image {:src "ch3ex34e.png"}])

(note-md "## 3.5 - Testing Independence by a Chi-square Test")

(note-def (def S (stats/chisq-test t2)))
(note ($ S 'expected))
(note (base/sum (rdiv (rpow (r- t2 ($ S 'expected)) 2)
                      ($ S 'expected))))
(note (r- 1 (stats/pchisq 54.57759 :df 9)))
(note (base/names S))
(note ($ S 'residuals))

(note-void (plot->file (str target-path "ch3ex34f.png") #(g/mosaicplot t2 :shade false :main "not shaded")))
(note-void (plot->file (str target-path "ch3ex34g.png") #(g/mosaicplot t2 :shade true :main "shaded")))
(note-hiccup  [:div
               [:image {:src "ch3ex34f.png"}]
               [:image {:src "ch3ex34g.png"}]])

(note-md "### Exercise 3.1 - Fast food eating preference")
(note-md "### Exercise 3.2 - Dice rolls")
(note-md "### Exercise 3.3 - Does baseball hitting data follow a binomial distribution?")
(note-md "### Exercise 3.4 - Categorizing ages in the twins dataset")
(note-md "### Exercise 3.5 - Relating age and wage in the twins dataset")
(note-md "### Exercise 3.6 - Relating age and wage in the twins dataset, continued")
(note-md "### Exercise 3.7 - Dice rolls, continued")
(note-md "### Exercise 3.8 - Are the digits of `pi` random?")

(note-md "# Cleaning")

(note-void (base/detach brain))
(note-void (base/detach USArrests))

(notespace.v2.note/compute-this-notespace!)

#_(r/discard-all-sessions)

