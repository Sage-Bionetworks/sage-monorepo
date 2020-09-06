clinical_outcomes_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      source_files <- c(
        "R/modules/server/submodules/clinical_outcomes_survival_server.R",
        "R/modules/server/submodules/clinical_outcomes_heatmap_server.R",
        "R/modules/ui/submodules/clinical_outcomes_survival_ui.R",
        "R/modules/ui/submodules/clinical_outcomes_heatmap_ui.R"
      )

      for (file in source_files) {
        source(file, local = T)
      }

      call_module_server2(
        "clinical_outcomes_survival",
        cohort_obj,
        server_function = clinical_outcomes_survival_server,
        ui_function = clinical_outcomes_survival_ui,
        test_function = shiny::reactive(show_co_submodules)
      )

      call_module_server2(
        "clinical_outcomes_heatmap",
        cohort_obj,
        server_function = clinical_outcomes_heatmap_server,
        ui_function = clinical_outcomes_heatmap_ui,
        test_function = shiny::reactive(show_co_submodules)
      )
    }
  )
}


