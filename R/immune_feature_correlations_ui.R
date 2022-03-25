immune_feature_correlations_ui <- function(id) {
    ns <- shiny::NS(id)
    iatlasModules::heatmap_ui(
        ns("heatmap")
        # html = shiny::includeMarkdown(
        #     get_markdown_path("immune_features_dist")
        # )
    )

    # shiny::tagList(
    #     iatlasModules::messageBox(
    #         width = 12,
    #         shiny::includeMarkdown(get_markdown_path(
    #             "immune_features_correlations"
    #         ))
    #     ),
    #     shiny::fluidRow(
    #         iatlasModules::optionsBox(
    #             width = 12,
    #             shiny::column(
    #                 width = 6,
    #                 shiny::uiOutput(ns("class_selection_ui"))
    #             ),
    #             shiny::column(
    #                 width = 3,
    #                 shiny::uiOutput(ns("response_selection_ui"))
    #             ),
    #             shiny::column(
    #                 width = 3,
    #                 shiny::selectInput(
    #                     ns("correlation_method"),
    #                     "Select or Search for Correlation Method",
    #                     choices = c(
    #                         "Pearson"  = "pearson",
    #                         "Spearman" = "spearman",
    #                         "Kendall"  = "kendall"
    #                     ),
    #                     selected = "spearman"
    #                 )
    #             )
    #         )
    #     ),
    #     shiny::fluidRow(
    #         iatlasModules::plotBox(
    #             width = 12,
    #             shiny::fluidRow(
    #                 "heatmap" %>%
    #                     ns() %>%
    #                     plotly::plotlyOutput(.) %>%
    #                     shinycssloaders::withSpinner(.),
    #                 iatlasModules::plotly_ui(ns("heatmap"))
    #             )
    #         )
    #     ),
    #     shiny::fluidRow(
    #         iatlasModules::plotBox(
    #             width = 12,
    #             "scatterPlot" %>%
    #                 ns() %>%
    #                 plotly::plotlyOutput(.) %>%
    #                 shinycssloaders::withSpinner(.),
    #             iatlasModules::plotly_ui(ns("scatterplot"))
    #         )
    #     )
    # )
}
