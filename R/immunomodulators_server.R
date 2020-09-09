immunomodulators_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      call_module_server(
        "distributions",
        cohort_obj,
        server_function = immunomodulator_distributions_server,
        ui_function = immunomodulators_distributions_ui
      )

      call_module_server(
        "datatable",
        cohort_obj,
        server_function = immunomodulator_datatable_server,
        ui_function = immunomodulators_datatable_ui
      )
    }
  )
}
