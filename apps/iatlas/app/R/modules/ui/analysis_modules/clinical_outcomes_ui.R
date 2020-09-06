clinical_outcomes_ui <- function(id) {
    ns <- shiny::NS(id)

    shiny::tagList(
        iatlas.app::titleBox("iAtlas Explorer — Clinical Outcomes"),
        iatlas.app::textBox(
            width = 12,
            shiny::includeMarkdown("markdown/clinical_outcomes.markdown")
        ),
        iatlas.app::sectionBox(
            title = "Sample Group Survival",
            module_ui(ns("clinical_outcomes_survival"))
        ),
        iatlas.app::sectionBox(
            title = "Concordance Index",
            module_ui(ns("clinical_outcomes_heatmap"))
        )
    )
}
