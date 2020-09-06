til_map_datatable_server <- function(id, cohort_obj, sample_tbl) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      tilmap_tbl <- shiny::reactive({
        shiny::req(sample_tbl())
        build_tm_dt_tbl(sample_tbl())
      })

      iatlas.app::data_table_server("til_table", tilmap_tbl, escape = F)
    }
  )
}
