data_info_ui <- function(id) {
    ns <- shiny::NS(id)

    shiny::tagList(
        iatlasModules::titleBox("iAtlas Explorer — Data Description"),
        iatlasModules::textBox(
            width = 12,
            shiny::includeMarkdown("inst/markdown/data_info.markdown")
        ),
        iatlasModules::sectionBox(
            title = "PanImmune Readouts",
            iatlasModules::messageBox(
                width = 12,
                shiny::includeMarkdown("inst/markdown/data_info_readouts.markdown")
            ),
            shiny::fluidRow(
                iatlasModules::optionsBox(
                    width = 6,
                    shiny::uiOutput(ns("classes")))),
            shiny::fluidRow(
                iatlasModules::tableBox(
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
