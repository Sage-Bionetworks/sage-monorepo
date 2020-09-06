clinical_outcomes_heatmap_ui <- function(id) {

    ns <- shiny::NS(id)

    shiny::tagList(
        iatlas.app::messageBox(
            width = 12,
            shiny::includeMarkdown("inst/markdown/clinical_outcomes_heatmap.markdown")
        ),
        shiny::fluidRow(
            iatlas.app::optionsBox(
                width = 12,
                shiny::column(
                    width = 6,
                    shiny::uiOutput(ns("time_feature_selection_ui"))
                ),
                shiny::column(
                    width = 6,
                    shiny::uiOutput(ns("class_selection_ui"))
                )
            ),
            iatlas.app::plotBox(
                width = 12,
                shiny::fluidRow(
                    "heatmap" %>%
                        ns() %>%
                        plotly::plotlyOutput(., height = 600) %>%
                        shinycssloaders::withSpinner(.),
                    plotly_ui(ns("heatmap"))
                )
            )
        )
    )
}
