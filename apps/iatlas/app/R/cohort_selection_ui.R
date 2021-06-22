cohort_selection_ui <- function(id) {
  ns <- shiny::NS(id)
  iatlas.modules2::cohort_selection_ui(ns("cohort_selection2"))
}
