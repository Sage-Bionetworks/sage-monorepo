immunomodulators_ui <- function(id) {

    ns <- shiny::NS(id)

    source("R/modules/ui/submodules/data_table_ui.R", local = T)
    source("R/modules/ui/submodules/distribution_plot_ui.R", local = T)
    source("R/modules/ui/ui_modules/distribution_plot_selector_ui.R", local = T)

    shiny::tagList(
        .GlobalEnv$titleBox("iAtlas Explorer — Immunomodulators"),
        .GlobalEnv$textBox(
            width = 12,
            shiny::includeMarkdown("markdown/immunomodulators.markdown")
        ),
        .GlobalEnv$sectionBox(
            title = "Immunomodulator Distributions",
            messageBox(
                width = 12,
                shiny::includeMarkdown(
                    "markdown/immunomodulators_distributions.markdown"
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
        ),
        data_table_ui(
            ns("im_table"),
            title = "Immunomodulator Annotations",
            message_html = shiny::includeMarkdown(
                "markdown/immunomodulators_dt.markdown"
            )
        )
    )
}
