immune_features_ui <- function(id) {

    ns <- shiny::NS(id)

    source_files <- c(
        "R/modules/ui/submodules/immune_feature_distributions_ui.R",
        "R/modules/ui/submodules/immune_feature_correlations_ui.R",
        "R/modules/ui/submodules/call_module_ui.R"
    )

    for (file in source_files) {
        source(file, local = T)
    }

    shiny::tagList(
        .GlobalEnv$titleBox("iAtlas Explorer — Immune Feature Trends"),
        .GlobalEnv$textBox(
            width = 12,
            shiny::p(paste0(
                "This module allows you to see how immune readouts vary ",
                "across your groups, and how they relate to one another."
            ))
        ),
        .GlobalEnv$sectionBox(
            title = "Correlations",
            call_module_ui(
                ns("immune_feature_distributions"),
                immune_feature_distributions_ui
            )
        ),
        .GlobalEnv$sectionBox(
            title = "Distributions",
            call_module_ui(
                ns("immune_feature_correlations"),
                immune_feature_correlations_ui
            )
        )
    )
}
