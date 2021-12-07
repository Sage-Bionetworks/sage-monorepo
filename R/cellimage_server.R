cellimage_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      show_submodule <- shiny::reactive({
        function(cohort_obj){
          print(length(cohort_obj$dataset_names))
          print(cohort_obj$dataset_names)
          print(cohort_obj$group_name)
          any(
            all(
              length(cohort_obj$dataset_names) == 1,
              cohort_obj$dataset_names == "TCGA",
              cohort_obj$group_name %in% c(
                "Immune_Subtype", "TCGA_Subtype", "TCGA_Study"
              )
            ),
            all(
              length(cohort_obj$dataset_names) == 1,
              cohort_obj$dataset_names == "PCAWG",
              cohort_obj$group_name %in% c(
                "Immune_Subtype", "PCAWG_Study"
              )
            )
          )
        }
      })

      call_module_server(
        "cellimage_main",
        cohort_obj,
        server_function = cellimage_main_server,
        ui_function =  cellimage_main_ui,
        test_function = show_submodule,
        warning_message = stringr::str_c(
          "The Extracellular Network is only currently computed for ",
          "dataset TCGA with groups Immune Subtype, TCGA Subtype, and TCGA Study, ",
          "and dataset PCAWG with groups Immune Subtype, and PCAWG Study, "
        )
      )
    }
  )
}



