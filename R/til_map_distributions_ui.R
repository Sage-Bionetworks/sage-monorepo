til_map_distributions_ui <- function(id) {

    ns <- shiny::NS(id)

    shiny::tagList(
        messageBox(
            width = 12,
            shiny::includeMarkdown(
                "inst/markdown/tilmap_dist.markdown"
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
        distribution_plot_ui(ns("tilmap_dist_plot"))
    )
}
