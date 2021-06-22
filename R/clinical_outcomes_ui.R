clinical_outcomes_ui <- function(id) {
  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::titleBox("iAtlas Explorer — Clinical Outcomes"),
    iatlas.modules::textBox(
      width = 12,
      shiny::includeMarkdown(get_markdown_path("clinical_outcomes"))
    ),
    iatlas.modules::sectionBox(
      title = "Sample Group Survival",
      module_ui(ns("clinical_outcomes_survival"))
    ),
    iatlas.modules::sectionBox(
      title = "Concordance Index",
      module_ui(ns("clinical_outcomes_heatmap"))
    )
  )
}
