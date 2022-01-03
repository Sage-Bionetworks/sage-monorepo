ici_neoantigen_correlations_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      #get the count data for the samples in the cohort_obj
      count_df <- arrow::read_feather("inst/feather/neoantigen_classes_count.feather")


      cohort_count <- shiny::reactive({
        cohort_patients <- cohort_obj()$sample_tbl %>%
          dplyr::inner_join(iatlas.api.client::query_sample_patients(), by = "sample_name") %>%
          dplyr::inner_join(count_df, by = "patient_name")
      })

      feature_classes <- shiny::reactive({
        cohort_obj()$feature_tbl$class %>%
          unique() %>%
          sort()
      })

      response_features <- shiny::reactive({
        unique(count_df$feature_name)

      })

      feature_data_function <- shiny::reactive({
        function(.feature_class){
          cohort_obj()$get_feature_values(feature_classes = .feature_class) %>%
            dplyr::select(
              "sample_name",
              "group_name" = "group_short_name",
              "feature_name",
              "feature_display",
              "feature_value",
              "feature_order",
              "group_description" = "group_characteristics",
              "group_color"
            )
        }
      })

      response_data_function <- shiny::reactive({
        View(cohort_obj())
        function(.feature){
          tbl <-
            cohort_count() %>%
            dplyr::select(
              "sample_name",
              "feature_name",
              "feature_display",
              "feature_value"
            )
        }
      })

      summarise_function_list = shiny::reactive({
        list(
          "Pearson" = purrr::partial(stats::cor, method = "pearson"),
          "Spearman" = purrr::partial(stats::cor, method = "spearman"),
          "Kendall" = purrr::partial(stats::cor, method = "kendall")
        )
      })

      result <- iatlas.modules::heatmap_server(
        "heatmap",
        feature_classes         = feature_classes,
        response_features       = response_features,
        feature_data_function   = feature_data_function,
        response_data_function  = response_data_function,
        summarise_function_list = summarise_function_list,
        drilldown               = shiny::reactive(T),
        default_class           = shiny::reactive(feature_classes()[[2]])
      )


    }
  )
}
