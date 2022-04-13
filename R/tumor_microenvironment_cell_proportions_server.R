tumor_microenvironment_cell_proportions_server  <- function(
  id,
  cohort_obj,
  mock_event_data = shiny::reactive(NULL)
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      sample_data_function <- shiny::reactive({
        function(.feature_class){

          result <-
            cohort_obj()$get_feature_values(features = c(
              "leukocyte_fraction", "Stromal_Fraction", "Tumor_fraction"
            )) %>%
            dplyr::select(
              "sample_name",
              "group_name" = "group_short_name",
              "feature_name",
              "feature_display",
              "feature_value"
            )
        }
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


      feature_data <- shiny::reactive({
        cohort_obj()$feature_tbl %>%
          dplyr::select(
            "feature_name" = "name",
            "feature_display" = "display"
          )
      })

      result <- iatlas.modules::barplot_server(
        "barplot",
        sample_data_function,
        group_data      = group_data,
        feature_data    = feature_data,
        barplot_xlab    = shiny::reactive("Fraction type by group"),
        barplot_ylab    = shiny::reactive("Fraction mean"),
        barplot_label   = shiny::reactive("Fraction"),
        drilldown       = shiny::reactive(T),
        y_feature_input = shiny::reactive("Leukocyte Fraction"),
        x_feature_input = shiny::reactive("Stromal Fraction"),
        mock_event_data = mock_event_data
      )
    }
  )
}
