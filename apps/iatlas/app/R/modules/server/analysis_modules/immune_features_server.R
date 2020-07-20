immune_features_server <- function(
    input,
    output,
    session,
    cohort_obj
){
    source_files <- c(
        "R/modules/server/submodules/immune_feature_distributions_server.R",
        "R/modules/server/submodules/immune_feature_correlations_server.R",
        "R/modules/server/submodules/call_module_server.R"
    )

    for (file in source_files) {
        source(file, local = T)
    }

    shiny::callModule(
        call_module_server,
        "immune_feature_distributions",
        cohort_obj,
        shiny::reactive(function(cohort_obj) T),
        immune_feature_distributions_server
    )

    shiny::callModule(
        call_module_server,
        "immune_feature_correlations",
        cohort_obj,
        shiny::reactive(function(cohort_obj) T),
        immune_feature_correlations_server
    )
}
