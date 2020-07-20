til_maps_server <- function(
    input,
    output,
    session,
    cohort_obj
){
    source_files <- c(
        "R/modules/server/submodules/til_map_distributions_server.R",
        "R/modules/server/submodules/til_map_datatable_server.R",
        "R/modules/server/submodules/call_module_server.R",
        "R/til_maps_functions.R"
    )

    for (file in source_files) {
        source(file, local = T)
    }

    tilmap_sample_tbl <- shiny::reactive({
        shiny::req(cohort_obj())
        build_tm_sample_tbl(cohort_obj()$sample_tbl)
    })

    shiny::callModule(
        call_module_server,
        "til_map_distributions",
        cohort_obj,
        shiny::reactive(show_tilmap_submodules),
        til_map_distributions_server,
        sample_tbl = tilmap_sample_tbl
    )

    shiny::callModule(
        call_module_server,
        "til_map_datatable",
        cohort_obj,
        shiny::reactive(show_tilmap_submodules),
        til_map_datatable_server,
        sample_tbl = tilmap_sample_tbl
    )
}
