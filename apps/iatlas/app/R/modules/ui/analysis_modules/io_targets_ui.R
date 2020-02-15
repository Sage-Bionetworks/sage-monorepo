io_targets_ui <- function(id) {

    ns <- shiny::NS(id)

    source("R/modules/ui/submodules/data_table_ui.R", local = T)
    source("R/modules/ui/submodules/distribution_plot_ui.R", local = T)
    source("R/modules/ui/ui_modules/distribution_plot_selector_ui.R", local = T)

    shiny::tagList(
        .GlobalEnv$titleBox("iAtlas Explorer — IO Targets"),
        .GlobalEnv$textBox(
            width = 12,
            shiny::includeMarkdown("markdown/io_target.markdown")
        ),
        .GlobalEnv$sectionBox(
            title = "IO Target Gene Expression Distributions",
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
        ),
        data_table_ui(
            ns("io_table"),
            title = "IO Target Annotations",
            message_html = shiny::includeMarkdown(
                "markdown/io_target_dt.markdown"
            )
        )
    )
}
