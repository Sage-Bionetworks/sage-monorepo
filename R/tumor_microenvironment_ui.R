tumor_microenvironment_ui <- function(id) {

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::titleBox("iAtlas Explorer — Tumor Microenvironment"),
    iatlas.modules::textBox(
      width = 12,
      shiny::includeMarkdown(get_markdown_path("tumor_microenvironment"))
    ),
    iatlas.modules::sectionBox(
      title = "Overall Cell Proportions",
      module_ui(ns("tumor_microenvironment_cell_proportions"))
    ),
    iatlas.modules::sectionBox(
      title = "Cell Type Fractions",
      module_ui(ns("tumor_microenvironment_type_fractions"))
    )
  )
}


