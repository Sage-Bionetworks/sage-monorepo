tumor_microenvironment_cell_proportions_server  <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      plot_data_function <- shiny::reactive({
        function(.feature_class){

          cohort_obj()$get_feature_values(features = c(
            "leukocyte_fraction", "Stromal_Fraction", "Tumor_fraction"
          )) %>%
            dplyr::select(
              "sample" = "sample_name",
              "group" = "group_short_name",
              "feature" = "feature_display",
              "feature_value",
              "group_description" = "group_characteristics"
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
