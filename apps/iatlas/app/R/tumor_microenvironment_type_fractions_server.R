tumor_microenvironment_type_fractions_server <- function(id, cohort_obj) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      plot_data_function <- shiny::reactive({
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
              "feature_value",
              "group_description" = "group_characteristics"
            )
        }
      })

      feature_classes <- shiny::reactive({
        c(
          "Immune Cell Proportion - Common Lymphoid and Myeloid Cell Derivative Class",
          "Immune Cell Proportion - Differentiated Lymphoid and Myeloid Cell Derivative Class",
          "Immune Cell Proportion - Multipotent Progenitor Cell Derivative Class",
          "Immune Cell Proportion - Original"
        )
      })

      result <- iatlas.modules::barplot_server(
        "barplot",
        plot_data_function,
        feature_classes,
        barplot_xlab  = shiny::reactive("Fraction type by group"),
        barplot_ylab  = shiny::reactive("Fraction mean"),
        barplot_label = shiny::reactive("Fraction"),
        drilldown     = shiny::reactive(T)
      )
    }
  )
}
