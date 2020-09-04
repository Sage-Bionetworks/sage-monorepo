cellimage_server <- function(
  input,
  output,
  session,
  cohort_obj
){

  source_files <- c(
    "R/modules/server/submodules/cellimage_main_server.R",
    "R/modules/server/submodules/call_module_server.R"
  )

  for (file in source_files) {
    source(file, local = T)
  }

  call_module_server(
    "cellimage_main",
    cohort_obj,
    shiny::reactive(function(cohort_obj) T),
    cellimage_main_server
  )
}



