til_maps_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      source_files <- c(
        "R/modules/server/submodules/til_map_distributions_server.R",
        "R/modules/server/submodules/til_map_datatable_server.R",
        "R/modules/ui/submodules/til_map_distributions_ui.R",
        "R/modules/ui/submodules/til_map_datatable_ui.R"
      )

      for (file in source_files) {
        source(file, local = T)
      }

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
