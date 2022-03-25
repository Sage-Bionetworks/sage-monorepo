clinical_outcomes_heatmap_ui <- function(id) {

    ns <- shiny::NS(id)

    shiny::tagList(
        iatlasModules::messageBox(
            width = 12,
            shiny::includeMarkdown("inst/markdown/clinical_outcomes_heatmap.markdown")
        ),
        shiny::fluidRow(
            iatlasModules::optionsBox(
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
            iatlasModules::plotBox(
                width = 12,
                shiny::fluidRow(
                    "heatmap" %>%
                        ns() %>%
                        plotly::plotlyOutput(., height = 600) %>%
                        shinycssloaders::withSpinner(.),
                    iatlasModules::plotly_ui(ns("heatmap"))
                )
            )
        )
    )
}
