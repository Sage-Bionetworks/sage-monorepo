io_targets_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {


      source_files <- c(
        "R/modules/ui/submodules/io_target_distributions_ui.R",
        "R/modules/ui/submodules/io_target_datatable_ui.R"
      )

      for (file in source_files) {
        source(file, local = T)
      }

      call_module_server(
        "distributions",
        cohort_obj,
        server_function = io_target_distributions_server,
        ui_function = io_target_distributions_ui
      )

      call_module_server(
        "datatable",
        cohort_obj,
        server_function = io_target_datatable_server,
        ui_function = io_target_datatable_ui
      )
    }
  )
}
