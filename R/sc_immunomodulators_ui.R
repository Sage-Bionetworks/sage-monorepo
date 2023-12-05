sc_immunomodulators_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::titleBox(
      "iAtlas Explorer — Immuno modulators in single-cell RNA-seq datasets"
    ),
    iatlas.modules::textBox(
      width = 12,
      p("This module allows you to see how immunomodulators are expressed in single-cell RNA-seq datasets.")
    ),
    iatlas.modules::sectionBox(
      title = "Distributions",
      sc_bubbleplot_ui(ns("sc_bubbleplot")),
      iatlas.modules::messageBox(
        width = 12,
        shiny::p("Distribution of gene expression for pseudobulk single-cell RNA-seq")
        #   shiny::includeMarkdown("inst/markdown/ici_immunefeatures.markdown")
      ),
      sc_immune_features_distribution_ui(ns("sc_immunomodulators_distribution"))
    )
  )
}
