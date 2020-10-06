immune_feature_correlations_ui <- function(id) {

    ns <- shiny::NS(id)

    shiny::tagList(
        messageBox(
            width = 12,
            shiny::includeMarkdown(get_markdown_path(
                "immune_features_correlations"
            ))
        ),
        shiny::fluidRow(
            optionsBox(
                width = 12,
                shiny::column(
                    width = 6,
                    shiny::uiOutput(ns("class_selection_ui"))
                ),
                shiny::column(
                    width = 3,
                    shiny::uiOutput(ns("response_selection_ui"))
                ),
                shiny::column(
                    width = 3,
                    shiny::selectInput(
                        ns("correlation_method"),
                        "Select or Search for Correlation Method",
                        choices = c(
                            "Pearson"  = "pearson",
                            "Spearman" = "spearman",
                            "Kendall"  = "kendall"
                        ),
                        selected = "spearman"
                    )
                )
            )
        ),
        shiny::fluidRow(
            plotBox(
                width = 12,
                shiny::fluidRow(
                    "heatmap" %>%
                        ns() %>%
                        plotly::plotlyOutput(.) %>%
                        shinycssloaders::withSpinner(.),
                    plotly_ui(ns("heatmap"))
                )
            )
        ),
        shiny::fluidRow(
            plotBox(
                width = 12,
                "scatterPlot" %>%
                    ns() %>%
                    plotly::plotlyOutput(.) %>%
                    shinycssloaders::withSpinner(.),
                plotly_ui(ns("scatterplot"))
            )
        )
    )
}
