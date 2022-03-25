model_selection_ui <- function(id){

    ns <- shiny::NS(id)

    shiny::fluidRow(
        iatlasModules::optionsBox(
            width = 12,
            shiny::column(
                width = 12,
                iatlasModules2::insert_remove_element_ui(
                    ns("select_numeric_covariate"),
                    "Add numerical covariate"
                )
            ),
            shiny::column(
                width = 12,
                iatlasModules2::insert_remove_element_ui(
                    ns("select_categorical_covariate"),
                    "Add categorical covariate"
                )
            )
        )
    )
}
