io_target_datatable_server <- function(
  input,
  output,
  session,
  cohort_obj
){

  source_files <- c(
    "R/modules/server/submodules/data_table_server.R"
  )

  for (file in source_files) {
    source(file, local = T)
  }

  data_table_server(
    "datatable",
    shiny::reactive(
      iatlas.app::build_io_target_dt_tbl(iatlas.api.client::query_io_targets())
    ),
    escape = F
  )
}
