til_maps_server <- function(
    input,
    output,
    session,
    sample_tbl,
    group_tbl,
    group_name,
    plot_colors
){

    source(
        "R/modules/server/submodules/til_map_distributions_server.R",
        local = T
    )

    source(
        "R/modules/server/submodules/til_map_datatable_server.R",
        local = T
    )

    source(
        "R/til_maps_functions.R",
        local = T
    )

    tilmap_sample_tbl <- shiny::reactive({
        shiny::req(sample_tbl())
        build_tm_sample_tbl(sample_tbl())
    })

    shiny::callModule(
        til_map_distributions_server,
        "til_map_distributions",
        tilmap_sample_tbl,
        group_tbl,
        group_name,
        plot_colors
    )

    shiny::callModule(
        til_map_datatable_server,
        "til_map_datatable",
        tilmap_sample_tbl
    )
}
