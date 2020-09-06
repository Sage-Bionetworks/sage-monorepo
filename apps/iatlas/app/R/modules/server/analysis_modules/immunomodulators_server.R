immunomodulators_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      source_files <- c(
        "R/modules/server/submodules/immunomodulator_distributions_server.R",
        "R/modules/server/submodules/immunomodulator_datatable_server.R",
        "R/modules/ui/submodules/immunomodulator_distributions_ui.R",
        "R/modules/ui/submodules/immunomodulator_datatable_ui.R"
      )

      for (file in source_files) {
        source(file, local = T)
      }

      call_module_server(
        "distributions",
        cohort_obj,
        server_function = immunomodulator_distributions_server,
        ui_function = immunomodulators_distributions_ui
      )

      call_module_server(
        "datatable",
        cohort_obj,
        server_function = immunomodulator_datatable_server,
        ui_function = immunomodulators_datatable_ui
      )
    }
  )
}
