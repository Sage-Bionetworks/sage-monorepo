data_info_ui <- function(id) {
    ns <- shiny::NS(id)

    shiny::tagList(
        .GlobalEnv$titleBox("iAtlas Explorer — Data Description"),
        .GlobalEnv$textBox(
            width = 12,
            shiny::includeMarkdown("markdown/data_info.markdown")
        ),
        .GlobalEnv$sectionBox(
            title = "PanImmune Readouts",
            .GlobalEnv$messageBox(
                width = 12,
                shiny::includeMarkdown("markdown/data_info_readouts.markdown")
            ),
            shiny::fluidRow(
                .GlobalEnv$optionsBox(
                    width = 6,
                    shiny::uiOutput(ns("classes")))),
            shiny::fluidRow(
                .GlobalEnv$tableBox(
                    width = 12,
                    shiny::div(
                        'feature_table' %>%
                            ns() %>%
                            DT::dataTableOutput() %>%
                            shinycssloaders::withSpinner()
                    )
                )
            )
        ),
        shiny::uiOutput(ns("variable_details_section"))
    )
}
