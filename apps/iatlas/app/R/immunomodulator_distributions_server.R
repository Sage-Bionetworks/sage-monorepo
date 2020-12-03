immunomodulator_distributions_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      features <- shiny::reactive({
        iatlas.api.client::query_immunomodulators() %>%
          dplyr::select(
            "feature_name" = "entrez",
            "feature_display" = "hgnc",
            "Gene Family" = "gene_family",
            "Gene Function" = "gene_function",
            "Immune Checkpoint" = "immune_checkpoint",
            "Super Category" = "super_category"
          )
      })

      plot_data_function <- shiny::reactive({
        function(.feature){
          group_data <- cohort_obj()$group_tbl %>%
            dplyr::select("group", "group_description" = "characteristics", "color")
          cohort_obj() %>%
            query_gene_expression_with_cohort_object(entrez = as.integer(.feature)) %>%
            dplyr::inner_join(cohort_obj()$sample_tbl, by = "sample") %>%
            dplyr::inner_join(group_data, by = "group") %>%
            dplyr::select(
              "sample",
              "group",
              "feature" = "hgnc",
              "feature_value" = "rna_seq_expr",
              "group_description",
              "color"
            )
        }
      })

      iatlas.modules::distributions_plot_server(
        "distplot",
        plot_data_function,
        features   = features,
        distplot_xlab = shiny::reactive(cohort_obj()$group_name),
        drilldown  = shiny::reactive(T)
      )
    }
  )
}
