driver_associations_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      source_files <- c(
        "R/modules/server/submodules/univariate_driver_server.R",
        "R/modules/server/submodules/multivariate_driver_server.R",
        "R/modules/server/submodules/call_module_server.R"
      )

      for (file in source_files) {
        source(file, local = T)
      }

      call_module_server(
        "univariate_driver",
        cohort_obj,
        shiny::reactive(iatlas.app::show_ud_submodule),
        univariate_driver_server
      )

      call_module_server(
        "multivariate_driver",
        cohort_obj,
        shiny::reactive(iatlas.app::show_md_submodule),
        multivariate_driver_server
      )
    }
  )
}



