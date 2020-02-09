immune_feature_distributions_ui <- function(id) {

    ns <- shiny::NS(id)

    source("R/modules/ui/submodules/plotly_ui.R", local = T)

    .GlobalEnv$sectionBox(
        title = "Distributions",
        .GlobalEnv$messageBox(
            width = 12,
            shiny::includeMarkdown(
                "markdown/immune_features_dist.markdown"
            ),
        ),
        shiny::fluidRow(
            .GlobalEnv$optionsBox(
                width = 12,
                shiny::column(
                    width = 4,
                    shiny::uiOutput(ns("selection_ui"))
                ),
                shiny::column(
                    width = 4,
                    shiny::selectInput(
                        ns("plot_type"),
                        "Select Plot Type",
                        choices = c("Violin", "Box")
                    )
                ),
                shiny::column(
                    width = 4,
                    shiny::selectInput(
                        ns("scale_method"),
                        "Select gene scaling",
                        selected = "None",
                        choices = c(
                            "None",
                            "Log2",
                            "Log2 + 1",
                            "Log10",
                            "Log10 + 1"
                        ),
                    )
                )
            )
        ),
        .GlobalEnv$plotBox(
            width = 12,
            "distplot" %>%
                ns() %>%
                plotly::plotlyOutput() %>%
                shinycssloaders::withSpinner(),
            plotly_ui(ns("immune_feature_dist_plot"))
        ),
        .GlobalEnv$plotBox(
            width = 12,
            "histplot" %>%
                ns() %>%
                plotly::plotlyOutput() %>%
                shinycssloaders::withSpinner(),
            plotly_ui(ns("immune_feature_hist_plot"))
        )
    )

}
