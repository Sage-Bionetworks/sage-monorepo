cellimage_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      call_module_server(
        "cellimage_main",
        cohort_obj,
        server_function = cellimage_main_server,
        ui_function =  cellimage_main_ui,
        test_function = shiny::reactive(show_ecn_submodules),
        warning_message = stringr::str_c(
          "The Extracellular Network is only currently computed for ",
          "dataset TCGA with groups Immune Subtype, TCGA Subtype, and TCGA Study, ",
          "and dataset PCAWG with groups Immune Subtype, and PCAWG Study, "
        )
      )
    }
  )
}



