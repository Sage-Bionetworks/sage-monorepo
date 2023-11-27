sc_immune_features_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::titleBox(
      "iAtlas Explorer — Immune Features in single cell RNA seq datasets"
    ),
    iatlas.modules::textBox(
      width = 12,
      p("This module allows you to see how immune readouts vary across cell types.")
    ),
    iatlas.modules::sectionBox(
      title = "Distributions",
      iatlas.modules::messageBox(
        width = 12,
        shiny::p("GSEA results")
      #   shiny::includeMarkdown("inst/markdown/ici_immunefeatures.markdown")
      ),
      sc_immune_features_distribution_ui(ns("sc_immune_features_distribution"))
    )
  )
}
