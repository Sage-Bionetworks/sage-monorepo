distribution_plot_ui <- function(id){

    ns <- shiny::NS(id)

    shiny::tagList(
        shiny::fluidRow(
            plotBox(
                width = 12,
                "distplot" %>%
                    ns() %>%
                    plotly::plotlyOutput(.) %>%
                    shinycssloaders::withSpinner(.),
                plotly_ui(ns("dist_plot"))
            )
        ),
        shiny::fluidRow(
            plotBox(
                width = 12,
                "histplot" %>%
                    ns() %>%
                    plotly::plotlyOutput(.) %>%
                    shinycssloaders::withSpinner(.),
                plotly_ui(ns("hist_plot"))
            )
        )
    )
}
