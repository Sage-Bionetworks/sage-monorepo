immune_features_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      source_files <- c(
        "R/modules/server/submodules/immune_feature_distributions_server.R",
        "R/modules/server/submodules/immune_feature_correlations_server.R"
      )

      for (file in source_files) {
        source(file, local = T)
      }

      call_module_server(
        "immune_feature_distributions",
        cohort_obj,
        server_function = immune_feature_distributions_server
      )

      call_module_server(
        "immune_feature_correlations",
        cohort_obj,
        server_function = immune_feature_correlations_server
      )
    }
  )
}
