til_map_distributions_ui <- function(id) {

    ns <- shiny::NS(id)

    source("R/modules/ui/submodules/distribution_plot_ui.R", local = T)
    source("R/modules/ui/ui_modules/distribution_plot_selector_ui.R", local = T)

    shiny::tagList(
        iatlas.app::messageBox(
            width = 12,
            shiny::includeMarkdown(
                "markdown/tilmap_dist.markdown"
            ),
        ),
        shiny::fluidRow(
            iatlas.app::optionsBox(
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
