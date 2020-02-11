clinical_outcomes_heatmap_ui <- function(id) {

    ns <- shiny::NS(id)

    source("R/modules/ui/submodules/plotly_ui.R", local = T)


    .GlobalEnv$sectionBox(
        title = "Concordance Index",
        .GlobalEnv$messageBox(
            width = 12,
            shiny::includeMarkdown("markdown/clinical_outcomes_heatmap.markdown")
        ),
        shiny::fluidRow(
            .GlobalEnv$optionsBox(
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
            .GlobalEnv$plotBox(
                width = 12,
                shiny::fluidRow(
                    "heatmap" %>%
                        ns() %>%
                        plotly::plotlyOutput(height = 600) %>%
                        shinycssloaders::withSpinner(),
                    plotly_ui(ns("heatmap"))
                )
            )
        )
    )
}
