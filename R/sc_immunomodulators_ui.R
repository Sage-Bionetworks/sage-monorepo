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
      # shiny::column(
      #   width = 6,
        shiny::tagList(
          iatlas.modules::messageBox(
            width = 24,
            shiny::p("See the percentage of cells from a given type that have counts for selected genes with average expression values.")
          ),
          shiny::verticalLayout(
            iatlas.modules::optionsBox(
              width=24,
              shiny::selectizeInput(
                ns("genes"),
                label = "Select or Search for genes",
                choices = NULL,
                multiple = TRUE
              )
            ),
            iatlas.modules::plotBox(
              width=24,
              plotly::plotlyOutput(ns("bubble_plot"))
            )
          )
        #)
      ),
      # shiny::column(
      #   width = 6,
        iatlas.modules::messageBox(
          width = 12,
          shiny::p("Distribution of gene expression for pseudobulk single-cell RNA-seq")
        ),
        sc_immune_features_distribution_ui(ns("sc_immunomodulators_distribution"))
      #)
  )
  )
}
