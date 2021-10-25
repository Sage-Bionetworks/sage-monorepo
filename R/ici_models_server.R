ici_models_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      predictors_list <- reactive({

        clinical_data <- iatlas.api.client::query_dataset_tags(datasets = cohort_obj()$dataset_names) %>%
          dplyr::filter(!tag_name %in% c("Response", "Responder", "Clinical_Benefit", "Progression", "Sample_Treatment", "TCGA_Study")) %>%
          dplyr::select(tag_short_display, tag_name) %>%
          tibble::deframe()

        immunefeatures <-  cohort_obj()$feature_tbl %>%
          dplyr::filter(class != "Predictor of Response to Immune Checkpoint Treatment" &
                          method_tag != "Survival") %>%
          iatlas.app::create_nested_list_by_class(
              class_column = "class",
              display_column = "display",
              internal_column = "name"
            )

        biomarkers <- cohort_obj()$feature_tbl %>%
          dplyr::filter(class == "Predictor of Response to Immune Checkpoint Treatment") %>%
          dplyr::select(display, name) %>%
          tibble::deframe()

        genes <- iatlas.api.client::query_immunomodulators() %>%
          iatlas.app::create_nested_list_by_class(
            class_column = "gene_family",
            display_column = "hgnc",
            internal_column = "entrez"
          )

        list(
          clinical_data = clinical_data,
          immunefeatures = immunefeatures,
          biomarkers = biomarkers,
          genes = genes
        )
      })

      ici_models_main_server(
        "ici_models_train",
        cohort_obj(),
        variables_list = predictors_list()
      )
    }
  )
}

