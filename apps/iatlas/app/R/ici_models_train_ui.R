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
      shiny::fluidRow(
        shiny::column(
          width = 3,
          shiny::selectInput(ns("train_method"),
                             label = "Select training method",
                             choices = "Elastic Net Regression",
                             selected = "Elastic Net Regression"),
          shiny::splitLayout(
            shiny::p("Add seed (optional)"),
            shiny::numericInput(ns("seed_value"), NULL, value = NULL, width = "80%")
          ),
          shiny::br(),
          shiny::sliderInput(ns("cv_number"),
                             label = "Number of folds for cross-validation",
                             min = 1,
                             max = 10,
                             step = 1,
                             value = 3)
          ),
          shiny::column(
            width = 3,
            shiny::p(strong("Select predictors")),
            shiny::selectizeInput(ns("predictors_clinical_data"),
                                  label = "Clinical Data",
                                  choices = NULL,
                                  multiple = TRUE),
            shiny::selectizeInput(ns("predictors_immunefeatures"),
                                  label = "Immunefeatures",
                                  choices = NULL,
                                  multiple = TRUE),
            shiny::selectizeInput(ns("predictors_biomarkers"),
                                  label = "Biomarkers",
                                  choices = NULL,
                                  multiple = TRUE),
            shiny::selectizeInput(ns("predictors_gene"),
                                  label = "Gene Expression",
                                  choices = NULL,
                                  multiple = TRUE)
          ),
        shiny::column(
          width = 6,
          shiny::hr(),
          textOutput(ns("samples_summary")),
          shiny::br(),
          textOutput(ns("train_summary")),
          shiny::br(),
          shiny::actionButton(ns("compute_train"), "Train Model")
        )
      )
    ),#optionsBox
    plotBox(
      width = 24,
      DT::dataTableOutput(ns("results")),
      plotly::plotlyOutput(ns("plot_coef")) %>%
        shinycssloaders::withSpinner()
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
    ),
    shiny::column(
      width = 5,
      tableBox(
        width = 24,
        shiny::p("Test results"),
        verbatimTextOutput(ns("accuracy")),
        plotOutput(ns("roc")),
        DT::dataTableOutput(ns("confusion_matrix"))
      )
    ),
    shiny::column(
      width = 7,
      plotBox(
        width = 24,
        shiny::p("KM plots dividing testing samples by predicted response"),
        uiOutput(ns("km_plots"))
      )
    )
  )
}
