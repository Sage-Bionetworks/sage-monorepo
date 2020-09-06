clinical_outcomes_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      call_module_server(
        "clinical_outcomes_survival",
        cohort_obj,
        server_function = clinical_outcomes_survival_server,
        ui_function = clinical_outcomes_survival_ui,
        test_function = shiny::reactive(show_co_submodules)
      )

      call_module_server(
        "clinical_outcomes_heatmap",
        cohort_obj,
        server_function = clinical_outcomes_heatmap_server,
        ui_function = clinical_outcomes_heatmap_ui,
        test_function = shiny::reactive(show_co_submodules)
      )
    }
  )
}


