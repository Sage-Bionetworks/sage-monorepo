immunomodulators_ui <- function(id) {

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlasModules::titleBox("iAtlas Explorer — Immunomodulators"),
    iatlasModules::textBox(
      width = 12,
      shiny::p(paste0(
        "Explore the expression of genes that code for ",
        "immunomodulating proteins, including checkpoint proteins."
      )),
    ),
    iatlasModules::sectionBox(
      title = "Immunomodulator Distributions",
      module_ui(ns("distributions"))
    ),
    iatlasModules::sectionBox(
      title = "Immunomodulator Annotations",
      module_ui(ns("datatable")
      )
    )
  )
}
