ici_models_train_ui <- function(id){
  ns <- shiny::NS(id)

  shiny::tagList(
    messageBox(
      width = 24,
      shiny::p("Select parameters of interest for model training")
    ),
    optionsBox(
      width = 24,
      shiny::column(
        width = 6,
        #shiny::p(strong("Select training and testing datasets")),
        shiny::uiOutput(ns("bucket_list"))
      ),
      shiny::column(
        width = 3,
        shiny::br(),
        shiny::selectInput(ns("train_method"),
                           label = "Select training method",
                           choices = "Elastic Net Regression",
                           selected = "Elastic Net Regression"),
        shiny::br(),
        shiny::sliderInput(ns("cv_number"),
                           label = "Number of folds for cross-validation",
                           min = 1,
                           max = 10,
                           step = 1,
                           value = 3),
        shiny::br(),
        shiny::selectizeInput(ns("train_predictors"),
                           label = "Select predictors",
                           choices = NULL,
                           multiple = TRUE)
      ),
      shiny::column(
        width = 3,
        shiny::br(),
        textOutput(ns("samples_summary")),
        shiny::br(),
        textOutput(ns("train_summary")),
        shiny::br(),
        shiny::actionButton(ns("compute_train"), "Train Model")
      )
    ),#optionsBox
    plotBox(
      width = 24
    ),
    optionsBox(
      width = 24,
      shiny::column(
        width = 5,
        shiny::p("Ready to see how your model performs on the test datasets?")
      ),
      shiny::column(
        width = 3,
        shiny::actionButton(ns("compute_test"), "Run model in the test dataset(s)")
      )
    )
  )
}
