data_info_ui <- function(id) {
    ns <- shiny::NS(id)

    shiny::tagList(
        titleBox("iAtlas Explorer — Data Description"),
        textBox(
            width = 12,
            shiny::includeMarkdown("inst/markdown/data_info.markdown")
        ),
        sectionBox(
            title = "PanImmune Readouts",
            messageBox(
                width = 12,
                shiny::includeMarkdown("inst/markdown/data_info_readouts.markdown")
            ),
            shiny::fluidRow(
                optionsBox(
                    width = 6,
                    shiny::uiOutput(ns("classes")))),
            shiny::fluidRow(
                tableBox(
                    width = 12,
                    shiny::div(
                        'feature_table' %>%
                            ns() %>%
                            DT::dataTableOutput(.) %>%
                            shinycssloaders::withSpinner(.)
                    )
                )
            )
        ),
        shiny::uiOutput(ns("variable_details_section"))
    )
}
