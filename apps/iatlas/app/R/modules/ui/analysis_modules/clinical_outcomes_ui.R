clinical_outcomes_ui <- function(id) {
    ns <- shiny::NS(id)
    source("R/modules/ui/submodules/clinical_outcomes_survival_ui.R", local = T)
    source("R/modules/ui/submodules/clinical_outcomes_heatmap_ui.R", local = T)
    source("R/modules/ui/submodules/call_module_ui.R", local = T)

    shiny::tagList(
        .GlobalEnv$titleBox("iAtlas Explorer — Clinical Outcomes"),
        .GlobalEnv$textBox(
            width = 12,
            shiny::includeMarkdown("markdown/clinical_outcomes.markdown")
        ),
        .GlobalEnv$sectionBox(
            title = "Sample Group Survival",
            call_module_ui(
                ns("clinical_outcomes_survival"),
                clinical_outcomes_survival_ui
            )
        ),
        .GlobalEnv$sectionBox(
            title = "Concordance Index",
            call_module_ui(
                ns("clinical_outcomes_heatmap"),
                clinical_outcomes_heatmap_ui
            )
        )
    )
}
