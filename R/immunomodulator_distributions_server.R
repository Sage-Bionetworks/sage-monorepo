immunomodulator_distributions_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      feature_data <- shiny::reactive({
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

      sample_data_function <- shiny::reactive({
        function(.feature){
          cohort_obj()$get_gene_values(entrez = as.integer(.feature)) %>%
            dplyr::select(
              "sample_name",
              "group_name" = "group_short_name",
              "feature_name" = "entrez",
              "feature_display" = "hgnc",
              "feature_value" = "rna_seq_expr",
              "group_description" = "group_characteristics",
              "group_color",
            ) %>%
            dplyr::mutate("dataset_name" = cohort_obj()$dataset_names)
        }
      })

      result <- iatlas.modules::distributions_plot_server(
        "distplot",
        sample_data_function = sample_data_function,
        feature_data = feature_data,
        distplot_xlab = shiny::reactive(cohort_obj()$group_display),
        scale_method_default = shiny::reactive("Log10"),
        drilldown  = shiny::reactive(T)
      )
    }
  )
}
