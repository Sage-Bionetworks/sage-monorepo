ici_overview_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::titleBox(
      "CRI iAtlas Explorer — Datasets of Treatment with Immune Checkpoint Inhibitors"
    ),
    iatlas.modules::textBox(
      width = 12,
      p("There are different types of data in CRI iAtlas, select the one you are interested.")
      # p("This module describes the datasets that are available in CRI iAtlas for the analysis of molecular response to
      #   Immune Checkpoint Inhibitor immunotherapy(ICI). Primary data processing and scoring of immune response, including
      #   immune signatures and cell-content estimates were performed by UNC Lineberger team Dante Bortone, Sarah Entwistle,
      #   led by Benjamin G. Vincent.")
    ),
    iatlas.modules::optionsBox(
      width = 12,
      shiny::radioButtons(
        ns("data_group"),
        "Select the type of data for more information",
        choices = c(
          "Immune Checkpoint Inhibitors datasets" = "ici",
          "Cancer Genomics datasets" = "cancer genomics",
          "Single-cell RNA-Seq datasets" = "single-cell RNA-Seq"
        ),
        selected = "ici"
      )
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
