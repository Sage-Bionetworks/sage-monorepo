til_maps_server <- function(
    input,
    output,
    session,
    cohort_obj
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

    # This is so that the conditional panel can see the various shiny::reactives
    output$display_til <- shiny::reactive(show_tilmap_submodules(cohort_obj()))
    shiny::outputOptions(output, "display_til", suspendWhenHidden = FALSE)

    tilmap_sample_tbl <- shiny::reactive({
        shiny::req(cohort_obj())
        build_tm_sample_tbl(cohort_obj()$sample_tbl)
    })

    shiny::callModule(
        til_map_distributions_server,
        "til_map_distributions",
        tilmap_sample_tbl,
        cohort_obj
    )

    shiny::callModule(
        til_map_datatable_server,
        "til_map_datatable",
        tilmap_sample_tbl
    )
}
