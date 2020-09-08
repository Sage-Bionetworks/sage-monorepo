univariate_driver_ui <- function(id){

    ns <- shiny::NS(id)

    shiny::tagList(
        messageBox(
            width = 12,
            shiny::includeMarkdown("inst/markdown/driver_single.markdown")
        ),
        shiny::fluidRow(
            optionsBox(
                width = 12,
                shiny::column(
                    width = 4,
                    shiny::uiOutput(ns("response_options"))
                ),
                shiny::column(
                    width = 4,
                    shiny::numericInput(
                        ns("min_mut"),
                        "Minimum number of mutant samples per group:",
                        20,
                        min = 2
                    )
                ),
                shiny::column(
                    width = 4,
                    shiny::numericInput(
                        ns("min_wt"),
                        "Minimum number of wild type samples per group:",
                        20,
                        min = 2
                    )
                )
            )
        ),
        shiny::fluidRow(
            plotBox(
                width = 12,
                "volcano_plot" %>%
                    ns() %>%
                    plotly::plotlyOutput(.) %>%
                    shinycssloaders::withSpinner(.),
                plotly_ui(ns("volcano_plot"))
            )
        ),
        shiny::fluidRow(
            plotBox(
                width = 12,
                "violin_plot" %>%
                    ns() %>%
                    plotly::plotlyOutput(.) %>%
                    shinycssloaders::withSpinner(.),
                plotly_ui(ns("violin_plot"))
            )
        )
    )
}
