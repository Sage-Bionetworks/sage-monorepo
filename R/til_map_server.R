til_map_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      call_module_server(
        "til_map_distributions",
        cohort_obj,
        test_function = shiny::reactive(show_tilmap_submodules),
        server_function = til_map_distributions_server,
        ui_function = til_map_distributions_ui
      )

      call_module_server(
        "til_map_datatable",
        cohort_obj,
        test_function = shiny::reactive(show_tilmap_submodules),
        server_function = til_map_datatable_server,
        ui_function = til_map_datatable_ui
      )
    }
  )
}
