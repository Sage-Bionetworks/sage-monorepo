driver_associations_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      source_files <- c(
        "R/modules/ui/submodules/univariate_driver_ui.R",
        "R/modules/ui/submodules/multivariate_driver_ui.R"
      )

      for (file in source_files) {
        source(file, local = T)
      }

      call_module_server(
        "univariate_driver",
        cohort_obj,
        test_function = shiny::reactive(iatlas.app::show_ud_submodule),
        server_function = univariate_driver_server,
        ui_function = univariate_driver_ui
      )

      call_module_server(
        "multivariate_driver",
        cohort_obj,
        test_function = shiny::reactive(iatlas.app::show_md_submodule),
        server_function = multivariate_driver_server,
        ui_function = multivariate_driver_ui
      )
    }
  )
}



