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
        width = 8,
        #shiny::p(strong("Select training and testing datasets")),
        shiny::uiOutput(ns("bucket_list"))
      ),
      shiny::column(
        width = 4,
        shiny::p(strong("2. Select predictors")),
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
      )
    ),
    shiny::fluidRow(
      shiny::column(
        width = 3,
        optionsBox(
          width = 12,
          shiny::selectInput(ns("train_method"),
                             label = "3. Select training method",
                             choices = "Elastic Net Regression",
                             selected = "Elastic Net Regression"),
          shiny::sliderInput(ns("cv_number"),
                             label = "Number of folds for cross-validation",
                             min = 1,
                             max = 10,
                             step = 1,
                             value = 3),
          optionsBox(
            width = 12,
            title = "Advanced",
            collapsible = TRUE,
            collapsed = TRUE,
            shiny::numericInput(ns("seed_value"), "Add seed", value = NULL, width = "80%"),
            shiny::br(),
            shiny::checkboxGroupInput(ns("exclude_previous"), "Exclude samples previously treated with:",
                                      choices = c("BRAF inhibitor" = "pre_BRAF",
                                                  "Ipilimumab" = "CTLA4_Pretreatment",
                                                  "MAPk inhibitor" = "Previous_MAPKi",
                                                  "Platinum" = "Received_platinum")),
            shiny::br(),
            shiny::p("Balance categorical classes in cross-validation folds"),
            shiny::checkboxInput(ns("balance_resp"), "Balance folds in cv for Response outcome", value = TRUE),
            shiny::checkboxInput(ns("balance_pred"), "Balance folds in cv for categorical predictors", value = FALSE),
            shiny::conditionalPanel(
              "input.balance_pred == 1",
              shiny::uiOutput(ns("categoric_pred")),
              ns = ns
            ),
            shiny::br(),
            shiny::selectInput(
              ns("scale_method"),
              "Scale numerical predictors",
              choices = c(
                "None",
                "Log2",
                "Log2 + 1",
                "Log10",
                "Log10 + 1"
              ),
              selected = "None"
            ),
            shiny::uiOutput(ns("num_transform")),
            shiny::br(),
            shiny::p("Transform numerical predictors"),
            shiny::checkboxInput(ns("do_norm"), "Normalize numeric data", value = TRUE)
          )
        )
      ),
      shiny::column(
        width = 9,
        shiny::verticalLayout(
          messageBox(
            width = 12,
            textOutput(ns("samples_summary")),
            shiny::br(),
            textOutput(ns("train_summary")),
            shiny::br(),
            shiny::actionButton(ns("compute_train"), "Train Model"),
            shiny::textOutput(ns("missing_data")),
            shiny::textOutput(ns("missing_sample"))
          ),
          plotBox(
            width = 12,
            DT::dataTableOutput(ns("results")),
            plotly::plotlyOutput(ns("plot_coef")) %>%
              shinycssloaders::withSpinner(),
            shiny::downloadButton(ns("download_train"), "Download training predictions")
          )
        )
      )
    ), #fluidRow
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
