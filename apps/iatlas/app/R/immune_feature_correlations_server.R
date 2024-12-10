immune_feature_correlations_server <- function(
  id,
  cohort_obj,
  mock_event_data = shiny::reactive(NULL)
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      feature_sample_data_function <- shiny::reactive({
        function(.feature_class){
          tbl <-
            cohort_obj()$get_feature_values(feature_classes = .feature_class) %>%
            dplyr::select(
              "sample_name",
              "group_name" = "group_short_name",
              "feature_name",
              "feature_display",
              "feature_value",
              "feature_order",
              "dataset_name"
            )
        }
      })

      response_sample_data_function <- shiny::reactive({
        function(.feature){
          tbl <-
            cohort_obj()$get_feature_values(features = .feature) %>%
            dplyr::select(
              "sample_name",
              "feature_name",
              "feature_value"
            )
        }
      })

      feature_data <- shiny::reactive({
        cohort_obj()$feature_tbl %>%
          dplyr::select(
            "feature_name" = "name",
            "feature_display" = "display",
            "feature_class" = "class",
            "feature_order" = "order"
          )
      })

      response_data <- shiny::reactive({
        cohort_obj()$feature_tbl %>%
          dplyr::select(
            "feature_name" = "name",
            "feature_display" = "display",
            "feature_class" = "class"
          ) %>%
          dplyr::arrange(.data$feature_class)
      })

      group_data <- shiny::reactive({
        cohort_obj()$group_tbl %>%
          dplyr::select(
            "group_name" = "short_name",
            "group_description" = "characteristics",
            "group_color" = "color"
          ) %>%
          dplyr::mutate("group_display" = .data$group_name)
      })

      summarise_function_list = shiny::reactive({
        list(
          "Pearson" = purrr::partial(stats::cor, method = "pearson"),
          "Spearman" = purrr::partial(stats::cor, method = "spearman"),
          "Kendall" = purrr::partial(stats::cor, method = "kendall")
        )
      })

      default_class <- shiny::reactive(
        feature_data()$feature_class[[1]]
      )

      result <- iatlas.modules::heatmap_server(
        "heatmap",

        feature_sample_data_function  = feature_sample_data_function,
        response_sample_data_function = response_sample_data_function,

        feature_data            = feature_data,
        response_data           = response_data,
        group_data              = group_data,
        summarise_function_list = summarise_function_list,
        drilldown               = shiny::reactive(T),
        default_class           = default_class,
        mock_event_data         = mock_event_data
      )

    }
  )
}

