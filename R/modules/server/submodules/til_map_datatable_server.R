til_map_datatable_server <- function(
    input,
    output,
    session,
    cohort_obj,
    sample_tbl
){

    source("R/modules/server/submodules/data_table_server.R", local = T)
    source("R/til_map_datatable_functions.R", local = T)

    tilmap_tbl <- shiny::reactive({
        shiny::req(sample_tbl())
        build_tm_dt_tbl(sample_tbl())
    })

    shiny::callModule(data_table_server, "til_table", tilmap_tbl, escape = F)
}
