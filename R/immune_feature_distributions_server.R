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
          cohort_obj()$get_feature_values(features = .feature) %>%
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

      result <- iatlas.modules::distributions_plot_server(
        "distplot",
        plot_data_function,
        features      = features,
        distplot_xlab = shiny::reactive(cohort_obj()$group_display),
        drilldown     = shiny::reactive(T)
      )

    }
  )
}
