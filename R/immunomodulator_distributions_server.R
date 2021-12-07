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
          cohort_obj()$get_gene_values(entrez = as.integer(.feature)) %>%
            dplyr::select(
              "sample" = "sample_name",
              "group" = "group_short_name",
              "feature" = "hgnc",
              "feature_value" = "rna_seq_expr",
              "group_description" = "group_characteristics",
              "color" = "group_color"
            )
        }
      })

      result <- iatlas.modules::distributions_plot_server(
        "distplot",
        plot_data_function,
        features   = features,
        distplot_xlab = shiny::reactive(cohort_obj()$group_display),
        scale_method_default = shiny::reactive("Log10"),
        drilldown  = shiny::reactive(T)
      )
    }
  )
}
