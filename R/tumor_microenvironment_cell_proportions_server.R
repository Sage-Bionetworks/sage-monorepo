tumor_microenvironment_cell_proportions_server  <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      plot_data_function <- shiny::reactive({
        function(.feature_class){
          group_data <- cohort_obj()$group_tbl %>%
            dplyr::select("group", "group_description" = "characteristics")

          cohort_obj() %>%
            query_feature_values_with_cohort_object(
              feature = c(
                "leukocyte_fraction", "Stromal_Fraction", "Tumor_fraction"
              )
            ) %>%
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
      })

      iatlas.modules::barplot_server(
        "barplot",
        plot_data_function,
        barplot_xlab    = shiny::reactive("Fraction type by group"),
        barplot_ylab    = shiny::reactive("Fraction mean"),
        barplot_label   = shiny::reactive("Fraction"),
        drilldown       = shiny::reactive(T),
        y_feature_input = shiny::reactive("Leukocyte Fraction"),
        x_feature_input = shiny::reactive("Stromal Fraction")
      )
    }
  )
}
