io_target_datatable_server <- function(
    input,
    output,
    session,
    cohort_obj
){

    source_files <- c(
        "R/modules/server/submodules/data_table_server.R",
        "R/io_target_functions.R"
    )

    for (file in source_files) {
        source(file, local = T)
    }

    shiny::callModule(
        data_table_server,
        "datatable",
        shiny::reactive(build_im_dt_tbl(build_im_tbl()))
    )
}
