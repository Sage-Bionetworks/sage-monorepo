ici_immunomodulators_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

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

      var_choices <- reactive({
        features() %>%
          filter(feature_display %in% colnames(ioresponse_data$im_expr)) %>%
          dplyr::mutate(INTERNAL = feature_display) %>%
          dplyr::select(
            INTERNAL,
            DISPLAY = feature_display,
            CLASS = `Gene Family`
          )
      })
      expr_data <- merge(ioresponse_data$fmx_df, ioresponse_data$im_expr, by = "Sample_ID")

      ici_distribution_server(
        "ici_immunomodulators_distribution",
        ioresponse_data,
        variable_options = var_choices(),
        metadata_feature_df = ioresponse_data$feature_df,
        feature_values = expr_data
      )
    }
  )
}
