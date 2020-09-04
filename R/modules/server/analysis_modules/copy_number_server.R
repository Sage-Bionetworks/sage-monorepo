copy_number_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      source_files <- c(
        "R/modules/server/submodules/copy_number_response_server.R",
        "R/modules/server/submodules/call_module_server.R",
        "R/copy_number_functions.R"
      )

      for (file in source_files) {
        source(file, local = T)
      }

      call_module_server(
        "copy_number_response",
        cohort_obj,
        shiny::reactive(function(cohort_obj) T),
        copy_number_response_server
      )
    }
  )
}



