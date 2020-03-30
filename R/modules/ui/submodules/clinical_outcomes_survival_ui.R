clinical_outcomes_survival_ui <- function(id) {

    ns <- shiny::NS(id)

    shiny::tagList(
        .GlobalEnv$messageBox(
            width = 12,
            shiny::includeMarkdown(
                "markdown/clinical_outcomes_survival.markdown"
            )
        ),
        shiny::fluidRow(
            .GlobalEnv$optionsBox(
                width = 12,
                shiny::column(
                    width = 8,
                    shiny::uiOutput(ns("time_feature_selection_ui"))
                ),
                shiny::column(
                    width = 2,
                    shiny::checkboxInput(
                        ns("confint"),
                        "Confidence Intervals",
                        value = F
                    )
                ),
                shiny::column(
                    width = 2,
                    shiny::checkboxInput(
                        ns("risktable"),
                        "Risk Table",
                        value = T
                    )
                )
            ),
            .GlobalEnv$plotBox(
                width = 12,
                "survival_plot" %>%
                    ns() %>%
                    shiny::plotOutput(height = 600) %>%
                    shinycssloaders::withSpinner()
            ),
            shiny::downloadButton(ns("download_tbl"), "Download plot table")
        )
    )
}
