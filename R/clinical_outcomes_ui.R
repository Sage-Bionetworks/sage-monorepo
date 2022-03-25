clinical_outcomes_ui <- function(id) {
  ns <- shiny::NS(id)

  shiny::tagList(
    iatlasModules::titleBox("iAtlas Explorer — Clinical Outcomes"),
    iatlasModules::textBox(
      width = 12,
      shiny::includeMarkdown(get_markdown_path("clinical_outcomes"))
    ),
    iatlasModules::sectionBox(
      title = "Sample Group Survival",
      module_ui(ns("clinical_outcomes_survival"))
    ),
    iatlasModules::sectionBox(
      title = "Concordance Index",
      module_ui(ns("clinical_outcomes_heatmap"))
    )
  )
}
