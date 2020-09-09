immune_feature_distributions_ui <- function(id) {

    ns <- shiny::NS(id)

    shiny::tagList(
        messageBox(
            width = 12,
            shiny::includeMarkdown(
                "inst/markdown/immune_features_dist.markdown"
            ),
        ),
        shiny::fluidRow(
            optionsBox(
                width = 12,
                shiny::column(
                    width = 4,
                    shiny::uiOutput(ns("selection_ui"))
                ),
                distribution_plot_selector_ui(id)
            )
        ),
        distribution_plot_ui(ns("immune_feature_dist_plot"))
    )
}
