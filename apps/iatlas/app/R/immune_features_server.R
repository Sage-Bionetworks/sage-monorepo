immune_features_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      call_module_server(
        "immune_feature_distributions",
        cohort_obj,
        server_function = immune_feature_distributions_server,
        ui_function = immune_feature_distributions_ui
      )

      call_module_server(
        "immune_feature_correlations",
        cohort_obj,
        server_function = immune_feature_correlations_server,
        ui_function = immune_feature_correlations_ui
      )
    }
  )
}
