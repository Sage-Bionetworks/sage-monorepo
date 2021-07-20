immune_feature_correlations_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      feature_classes <- shiny::reactive({
        cohort_obj()$feature_tbl$class
      })

      response_features <- shiny::reactive({
        cohort_obj()$feature_tbl %>%
          dplyr::arrange(.data$class) %>%
          iatlas.modules::create_nested_named_list(
            names_col1 = "class",
            names_col2 = "display",
            values_col = "name"
          )
      })

      feature_data_function <- shiny::reactive({
        function(.class){
          group_data <- cohort_obj()$group_tbl %>%
            dplyr::select("group", "group_description" = "characteristics", "color")

          tbl <-
            iatlas.modules2::query_feature_values_with_cohort_object(
              cohort_object = cohort_obj(),
              feature_classes = .class
            ) %>%
            dplyr::inner_join(cohort_obj()$sample_tbl, by = "sample") %>%
            dplyr::inner_join(group_data, by = "group") %>%
            dplyr::select(
              "sample",
              "group",
              "feature" = "feature_display",
              "feature_value",
              "feature_order",
              "group_description",
              "color"
            )
        }
      })

      response_data_function <- shiny::reactive({
        function(.feature){

          tbl <-
            iatlas.modules2::query_feature_values_with_cohort_object(
              cohort_object = cohort_obj(),
              features = .feature
            ) %>%
            dplyr::select(
              "sample",
              "feature" = "feature_display",
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
        drilldown               = shiny::reactive(T)
      )

    }
  )
}

