copy_number_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      call_module_server(
        "copy_number_response",
        cohort_obj,
        server_function = copy_number_response_server,
        ui_function = copy_number_response_ui
      )
    }
  )
}



