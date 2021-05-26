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
          shiny::actionButton(ns("compute_train"), "Train Model"),
          shiny::textOutput(ns("missing_data")),
          shiny::textOutput(ns("missing_sample"))
        )
      ),
      optionsBox(
        width = 24,
        title = "Advanced options",
        collapsible = TRUE,
        collapsed = TRUE,
        shiny::column(
          width = 4,
          shiny::checkboxInput(ns("do_norm"), "Normalize numeric data", value = TRUE),
          shiny::checkboxInput(ns("balance_resp"), "Balance folds in cv for Response outcome", value = TRUE)
        ),
        shiny::column(
          width = 4,
          shiny::checkboxInput(ns("balance_pred"), "Balance folds in cv for categorical predictors", value = FALSE),
          #add checkbox input for selected categorical predictors
        ),
        shiny::column(
          width = 4,
          shiny::checkboxInput(ns("transform_num"), "Transform numeric variables - before normalization, if selected", value = FALSE)
          #add checkbox input for selected numeric predictors
        )
      )
    ),#optionsBox
    plotBox(
      width = 24,
      DT::dataTableOutput(ns("results")),
      plotly::plotlyOutput(ns("plot_coef")) %>%
        shinycssloaders::withSpinner(),
      shiny::downloadButton(ns("download_train"), "Download training predictions")
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
      width = 12,
      plotBox(
        width = 24,
        uiOutput(ns("test_plots")),
        shiny::downloadButton(ns("download_test"), "Download testing predictions")
      )
    )
  )
}
