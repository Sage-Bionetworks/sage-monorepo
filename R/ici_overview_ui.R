ici_overview_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::titleBox(
      "iAtlas Explorer — Datasets of Treatment with Immune Checkpoint Inhibitors"
    ),
    iatlas.modules::textBox(
      width = 12,
      p("This module describes the datasets that are available in iAtlas for the analysis of molecular response to
        Immune Checkpoint Inhibitor immunotherapy(ICI). Primary data processing and scoring of immune response, including
        immune signatures and cell-content estimates were performed by UNC Lineberger team Dante Bortone, Sarah Entwistle,
        led by Benjamin G. Vincent.")
    ),
    iatlas.modules::sectionBox(
      title = "Dataset Information",
      ici_overview_datasets_ui(ns("ici_overview_datasets"))
    ),
    iatlas.modules::sectionBox(
      title = "Category Key",
      ici_overview_category_ui(ns("ici_overview_category"))
    )
  )
}
