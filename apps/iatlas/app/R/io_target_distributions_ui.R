io_target_distributions_ui <- function(id) {

    ns <- shiny::NS(id)

    shiny::tagList(
        messageBox(
            width = 12,
            shiny::includeMarkdown(
                "inst/markdown/io_target_dist.markdown"
            )
        ),
        shiny::fluidRow(
            optionsBox(
                width = 12,
                shiny::column(
                    width = 3,
                    shiny::selectInput(
                        inputId = ns("group_choice"),
                        label = "Select Group",
                        choices = c(
                            "Pathway" = "pathway",
                            "Therapy Type" = "therapy_type"
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
        distribution_plot_ui(ns("io_targets_dist_plot"))
    )
}
