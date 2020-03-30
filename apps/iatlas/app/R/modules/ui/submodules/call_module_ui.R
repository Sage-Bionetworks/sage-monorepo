call_module_ui <- function(
    id,
    ui_function,
    warning_message = "Currently selected cohort does not have the needed features to display this section"

) {

    ns <- shiny::NS(id)

    shiny::tagList(
        shiny::conditionalPanel(
            condition = "output.display_module",
            ui_function(ns("module")),
            ns = ns
        ),
        shiny::conditionalPanel(
            condition = "!output.display_module",
            .GlobalEnv$textBox(width = 12, warning_message),
            ns = ns
        )
    )
}
