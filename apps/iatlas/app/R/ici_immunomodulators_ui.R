ici_immunomodulators_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlasModules::titleBox(
      "iAtlas Explorer — Immunomodulators Expression in Immune Checkpoint Inhibitors datasets"
    ),
    iatlasModules::textBox(
      width = 12,
      p("Explore the expression of genes that code for immunomodulating proteins, including checkpoint proteins.")
    ),
    iatlasModules::sectionBox(
      title = "Distributions",
      iatlasModules::messageBox(width = 12,
                 shiny::includeMarkdown("inst/markdown/ici_immunomodulators.markdown")
                 ),
      ici_distribution_ui(ns("ici_immunomodulators_distribution"))
        )
      )
}
