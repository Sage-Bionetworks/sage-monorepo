ici_models_train_ui <- function(id){
  ns <- shiny::NS(id)

  shiny::tagList(
    verticalLayout(
      iatlas.modules::optionsBox(
        width = 12,
        shinyjs::useShinyjs(),
        shiny::column(
          width = 6,
          shiny::selectInput(ns("train_method"),
                             label = "Select training method",
                             choices = c("Elastic Net Regression", "Logistic Regression", "Random Forest", "XGBoost"),
                             selected = "Elastic Net Regression")
        ),
        shiny::column(
          width = 6,
          shiny::sliderInput(ns("cv_number"),
                             label = "Number of folds for cross-validation",
                             min = 1,
                             max = 10,
                             step = 1,
                             value = 3)
        ),
        shiny::column(width = 3),
        shiny::column(
          width = 3,
          shiny::actionButton(ns("compute_train"), "Train Model")
          )
      ),
      iatlas.modules::plotBox(
        width = 12,
        DT::dataTableOutput(ns("results")),
        plotly::plotlyOutput(ns("plot_coef")) %>%
          shinycssloaders::withSpinner(),
        shiny::downloadButton(ns("download_train"), "Download training predictions")
      )
    ),
    iatlas.modules::optionsBox(
      width = 12,
      shiny::actionButton(ns("compute_test"), "Run model in the test dataset(s)"),
      shiny::br(),
      shiny::selectInput(ns("test_survival"), "Select Survival Endpoint for KM plot with predicted outcome",
                         choices = c("Overall Survival" = "OS_time",
                                     "Progression Free Interval" = "PFI_time_1"),
                         selected = "OS_time")
    ),
    shiny::column(
      width = 12,
      iatlas.modules::plotBox(
        width = 24,
        uiOutput(ns("test_plots")),
        shiny::downloadButton(ns("download_test"), "Download testing predictions")
      )
    )
  )
}
