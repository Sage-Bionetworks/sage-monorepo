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

    sample_tbl2 <- shiny::reactive({
        shiny::req(sample_tbl())
        tbl <-
            paste(
                "SELECT s.id AS sample_id, s.name AS sample_name, ",
                "sl.name AS slide_barcode FROM samples s ",
                "INNER JOIN slides sl ON s.patient_id = sl.id ",
                "WHERE sl.name IS NOT NULL"
            ) %>%
            .GlobalEnv$perform_query("Get sample table") %>%
            dplyr::inner_join(sample_tbl(), by = "sample_id")
        return(tbl)
    })

    shiny::callModule(
        til_map_distributions_server,
        "til_map_distributions",
        sample_tbl2,
        group_tbl,
        group_name,
        plot_colors
    )

    shiny::callModule(
        til_map_datatable_server,
        "til_map_datatable",
        sample_tbl2
    )
}
