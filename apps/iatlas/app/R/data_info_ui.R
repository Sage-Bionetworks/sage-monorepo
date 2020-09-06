data_info_ui <- function(id) {
    ns <- shiny::NS(id)

    shiny::tagList(
        iatlas.app::titleBox("iAtlas Explorer — Data Description"),
        iatlas.app::textBox(
            width = 12,
            shiny::includeMarkdown("inst/markdown/data_info.markdown")
        ),
        iatlas.app::sectionBox(
            title = "PanImmune Readouts",
            iatlas.app::messageBox(
                width = 12,
                shiny::includeMarkdown("inst/markdown/data_info_readouts.markdown")
            ),
            shiny::fluidRow(
                iatlas.app::optionsBox(
                    width = 6,
                    shiny::uiOutput(ns("classes")))),
            shiny::fluidRow(
                iatlas.app::tableBox(
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
