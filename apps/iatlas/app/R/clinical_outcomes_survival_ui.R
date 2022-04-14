clinical_outcomes_survival_ui <- function(id) {

    ns <- shiny::NS(id)

    shiny::tagList(
        iatlas.modules::messageBox(
            width = 12,
            shiny::includeMarkdown(get_markdown_path(
              "clinical_outcomes_survival"
            ))
        ),
        shiny::fluidRow(
            iatlas.modules::optionsBox(
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
            iatlas.modules::plotBox(
                width = 12,
                "survival_plot" %>%
                    ns() %>%
                    shiny::plotOutput(., height = 600) %>%
                    shinycssloaders::withSpinner(.)
            ),
            shiny::downloadButton(ns("download_tbl"), "Download plot table")
        )
    )
}
