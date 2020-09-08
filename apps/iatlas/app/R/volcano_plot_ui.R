volcano_plot_ui <- function(id){

    ns <- shiny::NS(id)

    shiny::tagList(
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
