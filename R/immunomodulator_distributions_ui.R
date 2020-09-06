immunomodulators_distributions_ui <- function(id) {

    ns <- shiny::NS(id)

    shiny::tagList(
        messageBox(
            width = 12,
            shiny::includeMarkdown(
                "inst/markdown/immunomodulators_distributions.markdown"
            )
        ),
        shiny::fluidRow(
            iatlas.app::optionsBox(
                width = 12,
                shiny::column(
                    width = 3,
                    shiny::selectInput(
                        inputId = ns("gene_group_choice"),
                        label = "Select Group",
                        choices = c(
                            "Gene Family" = "gene_family",
                            "Super Category" = "super_category",
                            "Immune Checkpoint" = "immune_checkpoint"
                        )
                    )
                ),
                shiny::column(
                    width = 3,
                    shiny::uiOutput(ns("gene_choice_ui"))
                ),
                distribution_plot_selector_ui(id, scale_default = "Log10")
            )
        ),
        distribution_plot_ui(ns("immunomodulators_dist_plot"))
    )
}
