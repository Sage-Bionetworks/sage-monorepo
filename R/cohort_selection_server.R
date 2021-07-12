cohort_selection_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {
      cohort_obj <- iatlas.modules2::cohort_selection_server("cohort_selection")
      return(cohort_obj)
    }
  )
}

