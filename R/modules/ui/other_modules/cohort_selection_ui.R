cohort_selection_ui <- function(id) {

    ns <- shiny::NS(id)

    shiny::tagList(
        iatlas.app::titleBox("iAtlas Explorer — Cohort Selection"),
        iatlas.app::textBox(
            width = 12,
            shiny::includeMarkdown("markdown/cohort_selection1.markdown")
        ),
        iatlas.app::sectionBox(
            title = "Cohort Selection",
            iatlas.app::messageBox(
                width = 12,
                shiny::includeMarkdown("markdown/cohort_selection2.markdown"),
            ),
            shiny::fluidRow(
                iatlas.app::optionsBox(
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
        iatlas.app::sectionBox(
            title = "Group Key",
            data_table_ui(
                ns("sg_table"),
                message_html = shiny::p(paste0(
                    "This displays attributes and annotations of ",
                    "your choice of groups."
                ))
            )
        )

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
