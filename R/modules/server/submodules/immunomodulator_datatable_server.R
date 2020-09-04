immunomodulator_datatable_server <- function(
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
    "im_table",
    shiny::reactive(iatlas.app::build_im_dt_tbl())
  )
}
