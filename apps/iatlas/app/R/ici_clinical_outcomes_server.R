ici_clinical_outcomes_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {
      ici_clinical_outcomes_plot_server(
        "ici_clinical_outcomes_plot",
        cohort_obj
      )
    }
  )
}
