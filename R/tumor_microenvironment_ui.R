tumor_microenvironment_ui <- function(id) {

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlasModules::titleBox("iAtlas Explorer — Tumor Microenvironment"),
    iatlasModules::textBox(
      width = 12,
      shiny::includeMarkdown(get_markdown_path("tumor_microenvironment"))
    ),
    iatlasModules::sectionBox(
      title = "Overall Cell Proportions",
      module_ui(ns("tumor_microenvironment_cell_proportions"))
    ),
    iatlasModules::sectionBox(
      title = "Cell Type Fractions",
      module_ui(ns("tumor_microenvironment_type_fractions"))
    )
  )
}


