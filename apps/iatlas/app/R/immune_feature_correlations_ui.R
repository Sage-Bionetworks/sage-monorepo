immune_feature_correlations_ui <- function(id) {
    ns <- shiny::NS(id)
    iatlas.modules::heatmap_ui(
        ns("heatmap")
        # html = shiny::includeMarkdown(
        #     get_markdown_path("immune_features_dist")
        # )
    )

    # shiny::tagList(
    #     iatlas.modules::messageBox(
    #         width = 12,
    #         shiny::includeMarkdown(get_markdown_path(
    #             "immune_features_correlations"
    #         ))
    #     ),
    #     shiny::fluidRow(
    #         iatlas.modules::optionsBox(
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
    #         iatlas.modules::plotBox(
    #             width = 12,
    #             shiny::fluidRow(
    #                 "heatmap" %>%
    #                     ns() %>%
    #                     plotly::plotlyOutput(.) %>%
    #                     shinycssloaders::withSpinner(.),
    #                 iatlas.modules::plotly_ui(ns("heatmap"))
    #             )
    #         )
    #     ),
    #     shiny::fluidRow(
    #         iatlas.modules::plotBox(
    #             width = 12,
    #             "scatterPlot" %>%
    #                 ns() %>%
    #                 plotly::plotlyOutput(.) %>%
    #                 shinycssloaders::withSpinner(.),
    #             iatlas.modules::plotly_ui(ns("scatterplot"))
    #         )
    #     )
    # )
}
