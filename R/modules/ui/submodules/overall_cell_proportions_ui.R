overall_cell_proportions_ui <- function(id){

    ns <- shiny::NS(id)

    source("R/modules/ui/submodules/plotly_ui.R", local = T)

    .GlobalEnv$sectionBox(
        title = "Overall Cell Proportions",
        .GlobalEnv$messageBox(
            width = 12,
            shiny::includeMarkdown(
                "markdown/overall_cell_proportions1.markdown"
            )
        ),
        shiny::fluidRow(
            .GlobalEnv$plotBox(
                width = 12,
                "barplot" %>%
                    ns() %>%
                    plotly::plotlyOutput() %>%
                    shinycssloaders::withSpinner(),
                plotly_ui(ns("barplot"))
            )
        ),
        .GlobalEnv$messageBox(
            width = 12,
            shiny::includeMarkdown(
                "markdown/overall_cell_proportions2.markdown"
            )
        ),
        shiny::fluidRow(
            .GlobalEnv$plotBox(
                width = 12,
                "scatterplot" %>%
                    ns() %>%
                    plotly::plotlyOutput() %>%
                    shinycssloaders::withSpinner(),
                plotly_ui(ns("scatterplot"))
            )
        )
    )
}
