ici_neoantigen_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {
      ici_neoantigen_classes_server(
        "ici_neoantigen_classes",
        cohort_obj
      )
      ici_neoantigen_correlations_server(
        "ici_neoantigen_correlations",
        cohort_obj
      )
      ici_neoantigen_frequency_server(
        "ici_neoantigen_frequency",
        cohort_obj
      )
    }
  )
}
