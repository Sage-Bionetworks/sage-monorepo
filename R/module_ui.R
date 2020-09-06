module_ui <- function(id) {
    ns <- shiny::NS(id)
    shiny::uiOutput(ns("ui"))
}
