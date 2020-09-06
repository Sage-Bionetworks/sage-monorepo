cellimage_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      source("R/modules/ui/submodules/cellimage_main_ui.R")

      call_module_server2(
        "cellimage_main",
        cohort_obj,
        server_function = cellimage_main_server,
        ui_function =  cellimage_main_ui
      )
    }
  )
}



