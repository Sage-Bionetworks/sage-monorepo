copy_number_response_ui <- function(id){

    ns <- shiny::NS(id)

    tagList(
        textBox(
            width = 12,
            p("Explore statistical associations between immune readouts and copy number variations.")
        ),
        sectionBox(
            title = "Immune Response Association With Copy Number Variation",
            messageBox(
                width = 12,
                p("This module allows you to identify associations between specific somatic gene copy number alterations (SCNAs) and immune readouts. Gene SCNAs are specified as either amplified or deleted.\n\n"),

                p("The module begins with a large table of SCNA statistical associations (>3 Million) based on the t-test. Initially, statistics from all genes and all sample groups are shown.
                Use the filter controls to limit results to your interests.\n\n"),

                p("There are three components to the module: filter controls, a summary plot, and a table of results.\n"),

                p("The filter controls allow you to specify which subset of values are shown in the table and plot. It's possible to select multiple groups and genes."),

                p("The histogram shows the distribution of t-statistic values, given the filter settings."),

                tags$ul(
                    tags$li("The x-axis shows the t-statistic value, positive if the group of normal (un-altered) samples has higher immune readout scores, and negative if the opposite is true."),
                    tags$li("The y-axis represents the number of genes with that statistic.")
                ),
                p("\n\nImmune landscape manuscript context: The results are comparable to those shown in Figure S4A.","\n"),
                p(""),
                p("Notes: A statistical test is performed only when the number of samples exceeds a minimum required group count (currently 3).
                  In rare instances all (or all but one) samples within a group contain the alteration and a test cannot be performed.
                  Only statistics with p-values less than 0.001 are retained. As this module relies heavily on pre-computation, it is
                  not currently compatible with loading of user-defined custom sample groups."),
                p(""),
                p("")
            ),
            fluidRow(
                optionsBox(
                    width = 12,
                    column(
                        width = 6,
                        shiny::uiOutput(ns("response_option_ui"))
                    ),
                    column (
                        width = 6,
                        uiOutput(ns("select_cn_group_ui"))
                    ),
                    column (
                        width = 6,
                        uiOutput(ns("select_cn_gene_ui"))
                    ),
                    column (
                        width = 6,
                        selectInput(
                            ns("cn_dir_point_filter"),
                            "Select CNV Direction",
                            choices = c('All', 'Amp', 'Del'),
                            selected = "All"

                        )
                    ),
                    column(
                        width = 6,
                        textOutput(ns("text"))
                    )
                )
            ),
            fluidRow(
                plotBox(
                    width = 12,
                    plotly::plotlyOutput(ns("cnvPlot")) %>%
                        shinycssloaders::withSpinner()
                )
            ),

            tableBox(
                width = 12,
                div(style = "overflow-x: scroll",
                    DT::dataTableOutput(ns("cnvtable")) %>%
                        shinycssloaders::withSpinner()
                )
            )
        )
    )

}
