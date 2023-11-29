sc_immunomodulators_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      sc_bubbleplot_server(
        "sc_bubbleplot",
        cohort_obj
      )

      gsea_df <- shiny::reactive(arrow::read_feather("inst/feather/sc_pseudobulk_gene_expr.feather"))

      sc_immune_features_distribution_server(
        "sc_immunomodulators_distribution",
        cohort_obj,
        gsea_df
      )


    }
  )
}
