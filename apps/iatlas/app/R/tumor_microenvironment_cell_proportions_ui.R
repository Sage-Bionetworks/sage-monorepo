tumor_microenvironment_cell_proportions_ui <- function(id){

    ns <- shiny::NS(id)

    shiny::tagList(
        messageBox(
            width = 12,
            shiny::includeMarkdown(
                "inst/markdown/overall_cell_proportions1.markdown"
            )
        ),
        shiny::fluidRow(
            plotBox(
                width = 12,
                "barplot" %>%
                    ns() %>%
                    plotly::plotlyOutput(.) %>%
                    shinycssloaders::withSpinner(.),
                plotly_ui(ns("barplot"))
            )
        ),
        messageBox(
            width = 12,
            shiny::includeMarkdown(
                "markdown/overall_cell_proportions2.markdown"
            )
        ),
        shiny::fluidRow(
            plotBox(
                width = 12,
                "scatterplot" %>%
                    ns() %>%
                    plotly::plotlyOutput(.) %>%
                    shinycssloaders::withSpinner(.),
                plotly_ui(ns("scatterplot"))
            )
        )
    )
}
