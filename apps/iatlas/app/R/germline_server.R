germline_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      call_module_server(
        "germline_heritability",
        cohort_obj,
        server_function = germline_heritability_server,
        ui_function = germline_heritability_ui
      )

      call_module_server(
        "germline_gwas",
        cohort_obj,
        server_function = germline_gwas_server,
        ui_function = germline_gwas_ui
      )

      call_module_server(
        "germline_rarevariants",
        cohort_obj,
        server_function = germline_rarevariants_server,
        ui_function = germline_rarevariants_ui
      )

    }
  )
}



