tumor_microenvironment_type_fractions_server <- function(id, cohort_obj) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      plot_data_function <- shiny::reactive({
        func <- function(.feature_class){
          group_data <- cohort_obj()$group_tbl %>%
            dplyr::select("group", "group_description" = "characteristics")

          cohort_obj() %>%
            iatlas.modules2::query_feature_values_with_cohort_object(class = .feature_class) %>%
            dplyr::inner_join(cohort_obj()$sample_tbl, by = "sample") %>%
            dplyr::inner_join(group_data, by = "group") %>%
            dplyr::select(
              "sample",
              "group",
              "feature" = "feature_display",
              "feature_value",
              "group_description"
            )
        }
        return(func)
      })

      feature_classes <- shiny::reactive({
        c(
          "Immune Cell Proportion - Common Lymphoid and Myeloid Cell Derivative Class",
          "Immune Cell Proportion - Differentiated Lymphoid and Myeloid Cell Derivative Class",
          "Immune Cell Proportion - Multipotent Progenitor Cell Derivative Class",
          "Immune Cell Proportion - Original"
        )
      })

      iatlas.modules::barplot_server(
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
