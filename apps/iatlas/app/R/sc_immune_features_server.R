sc_immune_features_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      gsea_df <- shiny::reactive(arrow::read_feather("inst/feather/sc_gsea_norm.feather"))

      sc_immune_features_distribution_server(
        "sc_immune_features_distribution",
        cohort_obj,
        gsea_df()
      )


    }
  )
}
