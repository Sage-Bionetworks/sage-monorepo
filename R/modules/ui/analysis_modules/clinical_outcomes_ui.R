clinical_outcomes_ui <- function(id) {
    ns <- shiny::NS(id)
    source("R/modules/ui/submodules/clinical_outcomes_survival_ui.R", local = T)
    source("R/modules/ui/submodules/clinical_outcomes_heatmap_ui.R", local = T)
    source("R/modules/ui/submodules/call_module_ui.R", local = T)

    shiny::tagList(
        iatlas.app::titleBox("iAtlas Explorer — Clinical Outcomes"),
        iatlas.app::textBox(
            width = 12,
            shiny::includeMarkdown("markdown/clinical_outcomes.markdown")
        ),
        iatlas.app::sectionBox(
            title = "Sample Group Survival",
            call_module_ui(
                ns("clinical_outcomes_survival"),
                clinical_outcomes_survival_ui
            )
        ),
        iatlas.app::sectionBox(
            title = "Concordance Index",
            call_module_ui(
                ns("clinical_outcomes_heatmap"),
                clinical_outcomes_heatmap_ui
            )
        )
    )
}
