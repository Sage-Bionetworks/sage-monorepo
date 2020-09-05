immunomodulator_datatable_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      iatlas.app::data_table_server(
        "im_table",
        shiny::reactive(iatlas.app::build_im_dt_tbl())
      )
    }
  )
}
