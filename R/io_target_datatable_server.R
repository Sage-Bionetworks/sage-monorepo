io_target_datatable_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      data_table_server(
        "datatable",
        shiny::reactive(
          build_io_target_dt_tbl(
            iatlas.api.client::query_io_targets()
          )
        ),
        escape = F
      )
    }
  )
}
