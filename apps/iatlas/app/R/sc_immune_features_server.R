sc_immune_features_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      single_cell_datasets <- shiny::reactive(
        iatlasGraphQLClient::query_datasets(types = "scrna")
      )

      pseudobulk_df <- shiny::reactive({
        iatlasGraphQLClient::query_pseudobulk_feature_values() %>%
          dplyr::inner_join(cohort_obj()$sample_tbl, by = "sample_name") %>%
          dplyr::select(
            "sample_name",
            "group" = "cell_type",
            "feature_name" ,
            "feature_display",
            "feature_value" = "value",
            "dataset_name"
          )
      })

      sc_immune_features_distribution_server(
        "sc_immune_features_distribution",
        cohort_obj,
        pseudobulk_df,
        feature_op = dplyr::filter(cohort_obj()$feature_tbl, !class %in% c("Clinical", "umap"))
      )

      observeEvent(input$method_link,{
        shiny::showModal(modalDialog(
          title = "Method",
          includeMarkdown("inst/markdown/methods/sc-pseudobulk-scoring.md"),
          easyClose = TRUE,
          footer = NULL
        ))
      })


    }
  )
}
