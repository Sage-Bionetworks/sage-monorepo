data_info_ui <- function(id) {
    ns <- shiny::NS(id)

    shiny::tagList(
        iatlas.modules::titleBox("iAtlas Explorer — Data Description"),
        iatlas.modules::textBox(
            width = 12,
            shiny::includeMarkdown("inst/markdown/data_info.markdown")
        ),
        iatlas.modules::sectionBox(
            title = "PanImmune Readouts",
            iatlas.modules::messageBox(
                width = 12,
                shiny::includeMarkdown("inst/markdown/data_info_readouts.markdown")
            ),
            shiny::fluidRow(
                iatlas.modules::optionsBox(
                    width = 6,
                    shiny::uiOutput(ns("classes")))),
            shiny::fluidRow(
                iatlas.modules::tableBox(
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
