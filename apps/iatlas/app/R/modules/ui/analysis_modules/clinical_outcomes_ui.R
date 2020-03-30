clinical_outcomes_ui <- function(id) {
    ns <- shiny::NS(id)
    source("R/modules/ui/submodules/clinical_outcomes_survival_ui.R", local = T)
    source("R/modules/ui/submodules/clinical_outcomes_heatmap_ui.R", local = T)

    shiny::tagList(
        .GlobalEnv$titleBox("iAtlas Explorer — Clinical Outcomes"),
        .GlobalEnv$textBox(
            width = 12,
            shiny::includeMarkdown("markdown/clinical_outcomes.markdown")
        ),

        # Sample Group Survival ----
        .GlobalEnv$sectionBox(
            title = "Sample Group Survival",
            shiny::conditionalPanel(
                condition = "output.display_co",
                clinical_outcomes_survival_ui(ns("clinical_outcomes_survival")),
                ns = ns
            ),
            shiny::conditionalPanel(
                condition = "!output.display_co",
                .GlobalEnv$textBox(
                    width = 12,
                    paste0(
                        "Currently selected cohort does not have the needed ",
                        "features to show display this section"
                    )
                ),
                ns = ns
            )
        ),
        # Concordance Index ----
        .GlobalEnv$sectionBox(
            title = "Concordance Index",
            shiny::conditionalPanel(
                condition = "output.display_co",
                clinical_outcomes_heatmap_ui(ns("clinical_outcomes_heatmap")),
                ns = ns
            ),
            shiny::conditionalPanel(
                condition = "!output.display_co",
                .GlobalEnv$textBox(
                    width = 12,
                    paste0(
                        "Currently selected cohort does not have the needed ",
                        "features to show display this section"
                    )
                ),
                ns = ns
            )
        )
    )
}
