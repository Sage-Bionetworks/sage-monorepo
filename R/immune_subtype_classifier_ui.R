immune_subtype_classifier_ui <- function(id) {

  ns <- shiny::NS(id)

  shiny::tagList(

    iatlasModules::titleBox("iAtlas Tools — Immune Subtype Classifier"),

    iatlasModules::textBox(
      width = 12,
      shiny::p("Upload gene expression* and classify immune subtypes.")
    ),

    # Immunomodulator distributions section ----
    iatlasModules::sectionBox(
      title = "Immune Subtype Classification",

      iatlasModules::messageBox(
        width = 12,

        shiny::p("Upload gene expression (csv or tsv). **BETA** any gene quantification pipeline should be OK."),

        shiny::p(""),
        shiny::p(""),
        shiny::p("Notes on input data:"),
        shiny::tags$ul(
          shiny::tags$li("First row should be a header, with a 'GeneSymbol' column label, followed by sample IDs."),
          shiny::tags$li("First column should contain gene symbols, after that, samples."),
          shiny::tags$li("For an example of outputs, leave the input file blank, set ensemble size to a small number (32) and click GO.")
        ),
        shiny::p(""),
        shiny::tags$a(href="https://github.com/CRI-iAtlas/iatlas-app/blob/staging/inst/tsv/ebpp_test1_1to20.tsv", "Get an example input file here."),
        shiny::p(""),
        shiny::tags$hr(),
        shiny::p(shiny::tags$b("Data Formatting Example:")),
        shiny::p(""),
        shiny::p("GeneSymbol, Sample1, Sample2,..."),
        shiny::p("RKK1,       14.5,    100.1,..."),
        shiny::p("CMQ4,       1.10,    80.711,..."),
        shiny::p("....,       ....,    and etc..."),
        shiny::p(""),
        shiny::p(""),
        shiny::tags$hr(),

        shiny::p("Tool settings:"),
        shiny::tags$ul(
          shiny::tags$li(shiny::em('File separator'), ", select commas or tabs.")
        ),

        shiny::p(""),
        shiny::p("Notes on the classification"),
        shiny::tags$ul(
          shiny::tags$li("An ensemble of XGBoost classifiers was used."),
          shiny::tags$li("Robust features are computed that are not dependent on expression value.")
        ),
        shiny::p(""),
        shiny::p("Outputs:"),
        shiny::tags$ul(
          shiny::tags$li("Table shows TCGA reported subtypes with new aligned subtype calls."),
          shiny::tags$li("Barplot shows subtypes given to the new data."),
          shiny::tags$li("Table gives aligned subtypes, signature scores, and cluster probabilities.")
        ),
        shiny::p(""),
        shiny::p("Manuscript context:  See figure 1A.")
      ),
      shiny::fluidRow(
        iatlasModules::optionsBox(
          width = 12,
          shiny::column(
            width = 2,
            shiny::radioButtons(ns("sepa"), "File Separator",
                                choices = c(Tab = "\t", Comma = ","), selected = "\t")
          ),

          shiny::column(
            width = 4,
            shiny::fileInput(ns("expr_file_pred"), "Choose file. Leave blank for example run.",
                             multiple = FALSE,
                             accept = c("text/csv",
                                        "text/comma-separated-values,text/plain",
                                        ".csv",
                                        ".csv.gz",
                                        "text/tsv",
                                        "text/comma-separated-values,text/plain",
                                        ".tsv",
                                        ".tsv.gz"),
                             placeholder = 'data/ebpp_test1_1to20.tsv')
          ),

          shiny::column(
            width = 3,
            shiny::actionButton(ns("subtypeGObutton"), "GO")
          )
        )
      ),

      shiny::fluidRow(
        iatlasModules::plotBox(
          width = 12,
          shiny::plotOutput(ns('barPlot')) %>%
            shinycssloaders::withSpinner()
        )
      )
    ),

    # Immunomodulator annotations section ----
    iatlasModules::sectionBox(
      title = "Subtype Classification Table",
      iatlasModules::messageBox(
        width = 12,
        shiny::p("The table shows the results of subtype classification. Use the Search box in the upper right to find a sample of interest.")
      ),
      shiny::fluidRow(
        iatlasModules::tableBox(
          width = 12,
          shiny::div(style = "overflow-x: scroll",
                     DT::dataTableOutput(ns("subtypetable")) %>%
                       shinycssloaders::withSpinner(.)
          )
        )
      )
    )
  ) # taglist
}
