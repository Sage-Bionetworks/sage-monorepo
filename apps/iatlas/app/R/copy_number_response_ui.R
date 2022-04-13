copy_number_response_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::textBox(
      width = 12,
      shiny::p("Explore statistical associations between immune readouts and copy number variations.")
    ),
    iatlas.modules::sectionBox(
      title = "Immune Response Association With Copy Number Variation",
      iatlas.modules::messageBox(
        width = 12,
        shiny::p("This module allows you to identify associations between specific somatic gene copy number alterations (SCNAs) and immune readouts. Gene SCNAs are specified as either amplified or deleted.\n\n"),

        shiny::p("The module begins with a large table of SCNA statistical associations (>3 Million) based on the t-test. Initially, statistics from all genes and all sample groups are shown.
                Use the filter controls to limit results to your interests.\n\n"),

        shiny::p("There are three components to the module: filter controls, a summary plot, and a table of results.\n"),

        shiny::p("The filter controls allow you to specify which subset of values are shown in the table and plot. It's possible to select multiple groups and genes."),

        shiny::p("The histogram shows the distribution of t-statistic values, given the filter settings."),

        shiny::tags$ul(
          shiny::tags$li("The x-axis shows the t-statistic value, positive if the group of normal (un-altered) samples has higher immune readout scores, and negative if the opposite is true."),
          shiny::tags$li("The y-axis represents the number of genes with that statistic.")
        ),
        shiny::p("\n\nImmune landscape manuscript context: The results are comparable to those shown in Figure S4A.","\n"),
        shiny::p(""),
        shiny::p("Notes: A statistical test is performed only when the number of samples exceeds a minimum required group count (currently 3).
                  In rare instances all (or all but one) samples within a group contain the alteration and a test cannot be performed.
                  Only statistics with p-values less than 0.001 are retained. As this module relies heavily on pre-computation, it is
                  not currently compatible with loading of user-defined custom sample groups."),
        shiny::p(""),
        shiny::p("")
      ),
      shiny::fluidRow(
        iatlas.modules::optionsBox(
          width = 12,
          shiny::column(
            width = 6,
            shiny::uiOutput(ns("response_option_ui"))
          ),
          shiny::column (
            width = 6,
            shiny::uiOutput(ns("select_cn_group_ui"))
          ),
          shiny::column (
            width = 6,
            shiny::uiOutput(ns("select_cn_gene_ui"))
          ),
          shiny::column (
            width = 6,
            shiny::selectInput(
              ns("cn_dir_point_filter"),
              "Select CNV Direction",
              choices = c('All', 'Amp', 'Del'),
              selected = "All"

            )
          ),
          shiny::column(
            width = 6,
            shiny::textOutput(ns("text"))
          )
        )
      ),
      shiny::fluidRow(
        iatlas.modules::plotBox(
          width = 12,
          plotly::plotlyOutput(ns("cnvPlot")) %>%
            shinycssloaders::withSpinner(.)
        )
      ),

      iatlas.modules::tableBox(
        width = 12,
        shiny::div(style = "overflow-x: scroll",
                   DT::dataTableOutput(ns("cnvtable")) %>%
                     shinycssloaders::withSpinner(.)
        )
      )
    )
  )
}
