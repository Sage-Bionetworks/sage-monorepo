tumor_microenvironment_type_fractions_server <- function(
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
            cohort_obj()$get_feature_values(
              feature_classes = .feature_class
            ) %>%
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
            "feature_display" = "display",
            "feature_class" = "class"
          ) %>%
          dplyr::filter(
            .data$feature_class %in%
              c(
                "Immune Cell Proportion - Common Lymphoid and Myeloid Cell Derivative Class",
                "Immune Cell Proportion - Differentiated Lymphoid and Myeloid Cell Derivative Class",
                "Immune Cell Proportion - Multipotent Progenitor Cell Derivative Class",
                "Immune Cell Proportion - Original"
              )
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
        mock_event_data = mock_event_data
      )
    }
  )
}
