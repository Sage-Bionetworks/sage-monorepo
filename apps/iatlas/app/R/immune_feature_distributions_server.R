immune_feature_distributions_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      features <- shiny::reactive({
        cohort_obj()$feature_tbl %>%
          dplyr::select(
            "feature_class" = "class",
            "feature_name" = "name",
            "feature_display" = "display"
          )
      })

      plot_data_function <- shiny::reactive({
        function(.feature){
          group_data <- cohort_obj()$group_tbl %>%
            dplyr::select("group", "group_description" = "characteristics", "color")

          cohort_obj() %>%
            iatlas.modules2::query_feature_values_with_cohort_object(feature = .feature) %>%
            dplyr::inner_join(cohort_obj()$sample_tbl, by = "sample") %>%
            dplyr::inner_join(group_data, by = "group") %>%
            dplyr::select(
              "sample",
              "group",
              "feature" = "feature_display",
              "feature_value",
              "group_description",
              "color"
            )
        }
      })

      iatlas.modules::distributions_plot_server(
        "distplot",
        plot_data_function,
        features      = features,
        distplot_xlab = shiny::reactive(cohort_obj()$group_display),
        drilldown     = shiny::reactive(T)
      )

    }
  )
}
