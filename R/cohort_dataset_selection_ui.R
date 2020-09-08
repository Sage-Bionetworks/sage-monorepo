cohort_dataset_selection_ui <- function(id) {

    ns <- shiny::NS(id)

    shiny::tagList(
        shiny::fluidRow(
            optionsBox(
                width = 4,
                shiny::uiOutput(ns("dataset_selection_ui")),
            ),
        ),
        messageBox(
            width = 12,
            shiny::textOutput(ns("module_availibility_string"))
        )
    )
}
