immune_features_server <- function(
    input,
    output,
    session,
    cohort_obj
){

    source(
        "R/modules/server/submodules/immune_feature_distributions_server.R",
        local = T
    )
    source(
        "R/modules/server/submodules/immune_feature_correlations_server.R",
        local = T
    )

    shiny::callModule(
        immune_feature_distributions_server,
        "immune_feature_distributions",
        cohort_obj
    )


    shiny::callModule(
        immune_feature_correlations_server,
        "immune_feature_correlations",
        cohort_obj
    )
}
