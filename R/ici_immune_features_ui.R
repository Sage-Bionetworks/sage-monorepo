ici_immune_features_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlasModules::titleBox(
      "iAtlas Explorer — Immune Features in Immune Checkpoint Inhibitors datasets"
    ),
    iatlasModules::textBox(
      width = 12,
      p("This module allows you to see how immune readouts vary across your groups.")
      ),
    iatlasModules::sectionBox(
      title = "Distributions",
      iatlasModules::messageBox(
        width = 12,
        shiny::includeMarkdown("inst/markdown/ici_immunefeatures.markdown")
      ),
      ici_distribution_ui(ns("ici_immune_features_distribution"))
    )
  )
}
