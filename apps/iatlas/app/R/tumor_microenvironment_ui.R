tumor_microenvironment_ui <- function(id) {

  ns <- shiny::NS(id)

  shiny::tagList(
    titleBox("iAtlas Explorer — Tumor Microenvironment"),
    textBox(
      width = 12,
      shiny::includeMarkdown(get_markdown_path("tumor_microenvironment"))
    ),
    sectionBox(
      title = "Overall Cell Proportions",
      module_ui(ns("tumor_microenvironment_cell_proportions"))
    ),
    sectionBox(
      title = "Cell Type Fractions",
      module_ui(ns("tumor_microenvironment_type_fractions"))
    )
  )
}


