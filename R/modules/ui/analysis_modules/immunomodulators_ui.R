immunomodulators_ui <- function(id) {

    ns <- shiny::NS(id)

    source("R/modules/ui/submodules/data_table_ui.R", local = T)
    source("R/modules/ui/submodules/distribution_plot_ui.R", local = T)

    shiny::tagList(
        .GlobalEnv$titleBox("iAtlas Explorer — Immunomodulators"),
        .GlobalEnv$textBox(
            width = 12,
            shiny::p(stringr::str_c(
                "Explore the expression of genes that code for immunomodulating",
                "proteins, including checkpoint proteins.",
                sep = " "
            ))
        ),
        .GlobalEnv$optionsBox(
            width = 12,
            shiny::column(
                width = 4,
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
                width = 4,
                shiny::uiOutput(ns("gene_choice_ui"))
            )
        ),


        # distributions_plot_ui(
        #     ns("dist"),
        #     message_html = shiny::includeMarkdown("markdown/im_dist.markdown"),
        #     title_text = "Immunomodulator Distributions",
        #     scale_default = "Log10",
        #     plot_clicked_group_default = T,
        # ),

        data_table_ui(
            ns("im_table"),
            title = "Immunomodulator Annotations",
            message_html = shiny::p(stringr::str_c(
                "The table shows annotations of the immumodulators, and source.",
                "Use the Search box in the upper right to find an immumodulator of",
                "interest.",
                sep = " "
            ))
        )
    )
}
