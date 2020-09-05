tumor_microenvironment_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      source_files <- c(
        "R/modules/server/submodules/call_module_server.R"
      )

      for (file in source_files) {
        source(file, local = T)
      }

      call_module_server(
        "tumor_microenvironment_cell_proportions",
        cohort_obj,
        shiny::reactive(show_ocp_submodule),
        tumor_microenvironment_cell_proportions_server
      )

      call_module_server(
        "tumor_microenvironment_type_fractions",
        cohort_obj,
        shiny::reactive(show_ctf_submodule),
        tumor_microenvironment_type_fractions_server
      )
    }
  )
}
