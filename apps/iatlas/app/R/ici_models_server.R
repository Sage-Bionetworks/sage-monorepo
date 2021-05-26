ici_models_server <- function(
  id
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      predictors_list <- reactive({

        clinical_data <- ioresponse_data$feature_df %>%
          dplyr::filter(`Variable Class` == "Clinical data" | FeatureMatrixLabelTSV == "Subtype_Curated_Malta_Noushmehr_et_al") %>%
          dplyr::select(
            INTERNAL = FeatureMatrixLabelTSV,
            DISPLAY = FriendlyLabel,
            CLASS = `Variable Class`) %>% iatlas.app::create_nested_list_by_class()

        immunefeatures <-  ioresponse_data$feature_df %>%
          dplyr::filter(VariableType == "Numeric" &
                          !`Variable Class` %in% c("NA", "Clinical data", "Predictor - Immune Checkpoint Treatment")) %>%
          dplyr::select(
            INTERNAL = FeatureMatrixLabelTSV,
            DISPLAY = FriendlyLabel,
            CLASS = `Variable Class`) %>% iatlas.app::create_nested_list_by_class()

        biomarkers <- ioresponse_data$feature_df %>%
          dplyr::filter(
            `Variable Class` == "Predictor - Immune Checkpoint Treatment" | FeatureMatrixLabelTSV == "Subtype_Immune_Model_Based") %>%
          dplyr::select(
            INTERNAL = FeatureMatrixLabelTSV,
            DISPLAY = FriendlyLabel,
            CLASS = `Variable Class`) %>% iatlas.app::create_nested_list_by_class()


        gene_features <- iatlas.api.client::query_immunomodulators() %>%
          dplyr::select(
            "feature_name" = "entrez",
            "feature_display" = "hgnc",
            "Gene Family" = "gene_family",
            "Gene Function" = "gene_function",
            "Immune Checkpoint" = "immune_checkpoint",
            "Super Category" = "super_category"
          )

        genes <- gene_features %>%
          filter(feature_display %in% colnames(ioresponse_data$im_expr)) %>%
          mutate(INTERNAL = feature_display) %>%
          dplyr::select(
            INTERNAL,
            DISPLAY = feature_display,
            CLASS = `Gene Family`
          ) %>% iatlas.app::create_nested_list_by_class()

        list(
          clinical_data = clinical_data,
          immunefeatures = immunefeatures,
          biomarkers = biomarkers,
          genes = genes
        )
      })

      ici_models_train_server(
        "ici_models_train",
        variables_list = predictors_list()
      )
    }
  )
}

