cellimage_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      call_module_server(
        "cellimage_main",
        cohort_obj,
        server_function = cellimage_main_server,
        ui_function =  cellimage_main_ui
      )
    }
  )
}



