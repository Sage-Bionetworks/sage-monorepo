ici_models_main_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::messageBox(
      width = 24,
      shiny::p("Select parameters of interest for model training")
    ),
    iatlas.modules::optionsBox(
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
        iatlas.modules::optionsBox(
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
      ),
      shiny::column(
        width = 9,
        shiny::verticalLayout(
          iatlas.modules::messageBox(
            width = 12,
            textOutput(ns("samples_summary")),
            shiny::br(),
            textOutput(ns("train_summary")),
            shiny::br(),
            shiny::htmlOutput(ns("missing_data")),
            shiny::htmlOutput(ns("missing_level")),
            shiny::htmlOutput(ns("missing_sample"))
          )
        )
      ),
      shiny::column(
        width = 6,
        ici_models_train_ui(ns("ici_model1"))
      ),
      shiny::column(
        width = 6,
        ici_models_train_ui(ns("ici_model2"))
      )
    )
  )
}
