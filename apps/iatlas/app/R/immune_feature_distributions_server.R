immune_feature_distributions_server <- function(
  id,
  cohort_obj,
  mock_event_data = shiny::reactive(NULL)
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      sample_data_function <- shiny::reactive({
        function(.feature){
          cohort_obj()$get_feature_values(features = .feature) %>%
            dplyr::select(
              "sample_name",
              "group_name" = "group_short_name",
              "feature_name",
              "feature_display",
              "feature_value",
              "feature_order"
            ) %>%
            dplyr::mutate("dataset_name" = cohort_obj()$dataset_names)
        }
      })

      feature_data <- shiny::reactive({
        cohort_obj()$feature_tbl %>%
          dplyr::select(
            "feature_class" = "class",
            "feature_name" = "name",
            "feature_display" = "display"
          )
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

      result <- iatlas.modules::distributions_plot_server(
        "distplot",
        sample_data_function,
        feature_data    = feature_data,
        group_data      = group_data,
        distplot_xlab   = shiny::reactive(cohort_obj()$group_display),
        drilldown       = shiny::reactive(T),
        mock_event_data = mock_event_data
      )

    }
  )
}
