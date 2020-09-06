extracellular_network_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      call_module_server(
        "extracellular_network_main",
        cohort_obj,
        server_function = extracellular_network_main_server,
        ui_function = extracellular_network_main_ui
      )
    }
  )
}



