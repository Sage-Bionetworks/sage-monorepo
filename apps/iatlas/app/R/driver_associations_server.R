driver_associations_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      call_module_server(
        "univariate_driver",
        cohort_obj,
        test_function = shiny::reactive(show_ud_submodule),
        server_function = iatlas.modules2::univariate_driver_server,
        ui_function = iatlas.modules2::univariate_driver_ui
      )

      # call_module_server(
      #   "multivariate_driver",
      #   cohort_obj,
      #   test_function = shiny::reactive(show_md_submodule),
      #   server_function = iatlas.modules2::multivariate_driver_server,
      #   ui_function = iatlas.modules2::multivariate_driver_ui
      # )
    }
  )
}



