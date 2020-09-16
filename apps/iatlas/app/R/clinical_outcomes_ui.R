clinical_outcomes_ui <- function(id) {
  ns <- shiny::NS(id)

  shiny::tagList(
    titleBox("iAtlas Explorer — Clinical Outcomes"),
    textBox(
      width = 12,
      shiny::includeMarkdown(get_markdown_path("clinical_outcomes"))
    ),
    sectionBox(
      title = "Sample Group Survival",
      module_ui(ns("clinical_outcomes_survival"))
    ),
    sectionBox(
      title = "Concordance Index",
      module_ui(ns("clinical_outcomes_heatmap"))
    )
  )
}
