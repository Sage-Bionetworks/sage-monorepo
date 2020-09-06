immune_features_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      source_files <- c(
        "R/modules/server/submodules/immune_feature_distributions_server.R",
        "R/modules/server/submodules/immune_feature_correlations_server.R",
        "R/modules/ui/submodules/immune_feature_distributions_ui.R",
        "R/modules/ui/submodules/immune_feature_correlations_ui.R"
      )

      for (file in source_files) {
        source(file, local = T)
      }

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
