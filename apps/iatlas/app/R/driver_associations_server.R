driver_associations_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      show_submodule <- shiny::reactive({
        function(cohort_obj){
          all(
            length(cohort_obj$dataset_names) == 1,
            cohort_obj$dataset_names == "TCGA",
            cohort_obj$group_name %in% c(
              "Immune_Subtype", "TCGA_Subtype", "TCGA_Study"
            )
          )
        }
      })

      call_module_server(
        "univariate_driver",
        cohort_obj,
        test_function = show_submodule,
        server_function = iatlas.modules2::univariate_driver_server,
        ui_function = iatlas.modules2::univariate_driver_ui
      )

      # call_module_server(
      #   "multivariate_driver",
      #   cohort_obj,
      #   test_function = show_submodule,
      #   server_function = iatlas.modules2::multivariate_driver_server,
      #   ui_function = iatlas.modules2::multivariate_driver_ui
      # )
    }
  )
}



