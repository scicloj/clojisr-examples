(ns clojisr-examples.shiny
  (:require [clojisr.v1.r :as r :refer [r]]
            [clojisr.v1.require :refer [require-r]]))

(require-r '[base]
           '[shiny :as s])

;; Shiny's hello-world
;; https://shiny.rstudio.com/gallery/example-01-hello.html
;; Run this, and browse the url printed at the REPL.

(future
  (r.base/print
   (s/shinyApp
    (s/shinyUI
     (s/fluidPage
      (s/titlePanel "Hello Shiny!")
      (s/sidebarLayout
       (s/sidebarPanel
        (s/sliderInput
         "obs"
         "Number of observations"
         :min 1
         :max 1000
         :value 500))
       (s/mainPanel
        (s/plotOutput "distPlot")))))
    (s/shinyServer
     (r "function(input, output) {output$distPlot <- renderPlot(hist(rnorm(input$obs)))}")))))

;; To discard the current session running the Shiny app:
(r/discard-default-session)
