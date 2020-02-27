cohort_selection_ui <- function(id) {

    ns <- shiny::NS(id)

    source("R/modules/ui/submodules/cohort_manual_selection_ui.R", local = T)
    source("R/modules/ui/submodules/cohort_upload_selection_ui.R", local = T)
    source("R/modules/ui/submodules/data_table_ui.R", local = T)

    shiny::tagList(
        .GlobalEnv$titleBox("iAtlas Explorer — Cohort Selection"),
        .GlobalEnv$textBox(
            width = 12,
            shiny::includeMarkdown("markdown/cohort_selection1.markdown")
        ),
        .GlobalEnv$sectionBox(
            title = "Cohort Selection",
            .GlobalEnv$messageBox(
                width = 12,
                shiny::includeMarkdown("markdown/cohort_selection2.markdown"),
            ),
            shiny::fluidRow(
                .GlobalEnv$optionsBox(
                    width = 12,
                    shiny::column(
                        width = 4,
                        shiny::selectInput(
                            inputId = ns("cohort_mode_choice"),
                            label   = "Select Cohort Selection Mode",
                            choices = c("Cohort Selection", "Cohort Upload")
                        )
                    )
                )
            ),
            shiny::conditionalPanel(
                condition = "input.cohort_mode_choice == 'Cohort Selection'",
                cohort_manual_selection_ui(ns("cohort_manual_selection")),
                ns = ns
            ),
            shiny::conditionalPanel(
                condition = "input.cohort_mode_choice == 'Cohort Upload'",
                cohort_upload_selection_ui(ns("cohort_upload_selection")),
                ns = ns
            )
        ),
        data_table_ui(
            ns("sg_table"),
            title = "Group Key",
            message_html = shiny::p(stringr::str_c(
                "This displays attributes and annotations of your choice of",
                "groups.",
                sep = " "
            ))
        ),

        # sectionBox(
        #     title = "Group Overlap",
        #     messageBox(
        #         width = 12,
        #         includeMarkdown("markdown/sample_groups_overlap.markdown")
        #     ),
        #     fluidRow(
        #         optionsBox(
        #             width = 12,
        #             uiOutput(ns("mosaic_group_select")),
        #             uiOutput(ns("mosaic_subset_select"))
        #         ),
        #     ),
        #     fluidRow(
        #         plotBox(
        #             width = 12,
        #             column(
        #                 width = 12,
        #                 plotly::plotlyOutput(ns("mosaicPlot"), height = "600px") %>%
        #                     shinycssloaders::withSpinner()
        #             )
        #         )
        #     )
        # )
    )
}
