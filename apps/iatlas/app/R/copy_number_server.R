copy_number_server <- function(
  id,
  cohort_obj
){
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
        "copy_number_response",
        cohort_obj,
        server_function = copy_number_response_server,
        ui_function = copy_number_response_ui,
        test_function = show_submodule
      )
    }
  )
}



