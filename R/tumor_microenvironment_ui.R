tumor_microenvironment_ui <- function(id) {

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::titleBox("CRI iAtlas Explorer — Tumor Microenvironment"),
    iatlas.modules::textBox(
      width = 12,
      shiny::includeMarkdown(get_markdown_path("tumor_microenvironment")),
      shiny::p("Check out our ",
               a(href="https://github.com/CRI-iAtlas/iatlas-notebooks/blob/main/cell_content_barplots.ipynb", "Jupyter notebook"),
               "reproducing the code run in this module.")
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


