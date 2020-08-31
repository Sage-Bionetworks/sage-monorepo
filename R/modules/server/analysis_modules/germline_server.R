germline_server <- function(
    input,
    output,
    session,
    cohort_obj
){

  source_files <- c(
    "R/modules/server/submodules/germline_heritability_server.R",
    "R/modules/server/submodules/germline_gwas_server.R",
    "R/modules/server/submodules/call_module_server.R"
  )

  for (file in source_files) {
    source(file, local = T)
  }

  shiny::callModule(
    call_module_server,
    "germline_heritability",
    cohort_obj,
    shiny::reactive(function(cohort_obj) T),
    germline_heritability_server
  )

  shiny::callModule(
    call_module_server,
    "germline_gwas",
    cohort_obj,
    shiny::reactive(function(cohort_obj) T),
    germline_gwas_server
  )
}



