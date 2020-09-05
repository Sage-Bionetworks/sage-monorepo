io_targets_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {


      source_files <- c(
        "R/modules/server/submodules/io_target_distributions_server.R",
        "R/modules/server/submodules/io_target_datatable_server.R"
      )

      for (file in source_files) {
        source(file, local = T)
      }

      call_module_server(
        "distributions",
        cohort_obj,
        server_function = io_target_distributions_server
      )

      call_module_server(
        "datatable",
        cohort_obj,
        server_function = io_target_datatable_server
      )
    }
  )
}
