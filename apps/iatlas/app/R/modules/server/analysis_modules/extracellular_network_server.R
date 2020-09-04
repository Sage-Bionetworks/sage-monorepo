extracellular_network_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      source_files <- c(
        "R/modules/server/submodules/extracellular_network_main_server.R",
        "R/modules/server/submodules/call_module_server.R",
        "R/extracellular_network_functions.R"
      )

      for (file in source_files) {
        source(file, local = T)
      }

      call_module_server(
        "extracellular_network_main",
        cohort_obj,
        shiny::reactive(function(cohort_obj) T),
        extracellular_network_main_server
      )
    }
  )
}



