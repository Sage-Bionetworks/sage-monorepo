copy_number_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      source_files <- c(
        "R/modules/server/submodules/copy_number_response_server.R",
        "R/modules/ui/submodules/copy_number_response_ui.R"
      )

      for (file in source_files) {
        source(file, local = T)
      }

      call_module_server(
        "copy_number_response",
        cohort_obj,
        server_function = copy_number_response_server,
        ui_function = copy_number_response_ui
      )
    }
  )
}



