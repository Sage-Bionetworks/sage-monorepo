tumor_microenvironment_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      source("R/modules/ui/submodules/tumor_microenvironment_cell_proportions_ui.R", local = T)
      source("R/modules/ui/submodules/tumor_microenvironment_type_fractions_ui.R", local = T)

      call_module_server(
        "tumor_microenvironment_cell_proportions",
        cohort_obj,
        test_function = shiny::reactive(show_ocp_submodule),
        server_function = tumor_microenvironment_cell_proportions_server,
        ui_function = tumor_microenvironment_cell_proportions_ui
      )

      call_module_server(
        "tumor_microenvironment_type_fractions",
        cohort_obj,
        test_function = shiny::reactive(show_ctf_submodule),
        server_function = tumor_microenvironment_type_fractions_server,
        ui_function = tumor_microenvironment_type_fractions_ui
      )
    }
  )
}
