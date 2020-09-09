til_maps_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      tilmap_sample_tbl <- shiny::reactive({
        shiny::req(cohort_obj())
        build_tm_sample_tbl(cohort_obj()$sample_tbl)
      })

      call_module_server(
        "til_map_distributions",
        cohort_obj,
        test_function = shiny::reactive(show_tilmap_submodules),
        server_function = til_map_distributions_server,
        ui_function = til_map_distributions_ui,
        sample_tbl = tilmap_sample_tbl
      )

      call_module_server(
        "til_map_datatable",
        cohort_obj,
        test_function = shiny::reactive(show_tilmap_submodules),
        server_function = til_map_datatable_server,
        ui_function = til_map_datatable_ui,
        sample_tbl = tilmap_sample_tbl
      )
    }
  )
}
