til_map_datatable_server <- function(id, cohort_obj) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      #TODO: fix query: https://gitlab.com/cri-iatlas/iatlas-api/-/issues/30

      # tilmap_tbl <- shiny::reactive({
      #   shiny::req(sample_tbl())
      #   build_tm_dt_tbl(sample_tbl())
      # })
      #
      # data_table_server("til_table", tilmap_tbl, escape = F)
    }
  )
}
