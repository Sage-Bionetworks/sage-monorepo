io_target_distributions_ui <- function(id) {

    ns <- shiny::NS(id)

    source_files <- c(
        "R/modules/ui/submodules/distribution_plot_ui.R",
        "R/modules/ui/ui_modules/distribution_plot_selector_ui.R"
    )

    for (file in source_files) {
        source(file, local = T)
    }

    shiny::tagList(
        messageBox(
            width = 12,
            shiny::includeMarkdown(
                "markdown/io_target_dist.markdown"
            )
        ),
        shiny::fluidRow(
            .GlobalEnv$optionsBox(
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
