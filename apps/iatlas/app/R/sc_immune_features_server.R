sc_immune_features_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      single_cell_datasets <- shiny::reactive(
        iatlasGraphQLClient::query_datasets(types = "scrna")
      )

      pseudobulk_df <- shiny::reactive({
        iatlasGraphQLClient::query_pseudobulk_feature_values() %>% #TODO: update when data in cohort_obj
          dplyr::inner_join(iatlasGraphQLClient::query_dataset_samples(single_cell_datasets()$name), by = "sample_name") %>%
          dplyr::select(
            "sample_name",
            "group" = "cell_type",
            "feature_name" ,
            "feature_display",
            "feature_value" = "value",
            "dataset_name",
            "dataset_display"
          )
      })

      categories_df <- shiny::reactive(iatlasGraphQLClient::query_tags(datasets = single_cell_datasets()$name) %>% #TODO: update when data is in cohort_obj
                                         dplyr::mutate(class = dplyr::case_when(
                                           tag_name %in% c( "Response", "Responder", "Progression", "Clinical_Benefit") ~ "Response to ICI",
                                           TRUE ~ "Treatment Data")) %>%
                                         create_nested_list_by_class(.,
                                                                     class_column = "class",
                                                                     internal_column = "tag_name",
                                                                     display_column = "tag_short_display")
      )

      sc_immune_features_distribution_server(
        "sc_immune_features_distribution",
        cohort_obj,
        pseudobulk_df,
        feature_op = shiny::reactive(c( #TODO: change this when data is in cohort_obj
          "Wound Healing" = "CHANG_CORE_SERUM_RESPONSE_UP",
          "Macrophage Regulation" = "CSF1_response",
          "Lymphocyte Infiltration" = "LIexpression_score",
          "Proliferation" = "Module11_Prolif_score",
          "IFN-gamma Response" = "Module3_IFN_score",
          "TGF-beta Response" = "TGFB_score_21050467",
          "Th1 Cells" = "Th1_cells",
          "Th2 Cells" = "Th2_cells"
        )),
        categories_df
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
