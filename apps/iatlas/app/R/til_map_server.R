til_map_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      show_submodule <- shiny::reactive({
        function(cohort_obj){
          cohort_obj$has_classes("TIL Map Characteristic")
        }
      })

      call_module_server(
        "til_map_distributions",
        cohort_obj,
        test_function = show_submodule,
        server_function = til_map_distributions_server,
        ui_function = til_map_distributions_ui
      )

      # call_module_server(
      #   "til_map_datatable",
      #   cohort_obj,
      #   test_function = show_submodule,
      #   server_function = til_map_datatable_server,
      #   ui_function = til_map_datatable_ui
      # )
    }
  )
}
