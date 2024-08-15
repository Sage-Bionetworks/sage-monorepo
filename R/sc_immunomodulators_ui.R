sc_immunomodulators_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::titleBox(
      "iAtlas Explorer — Immuno modulators in single-cell RNA-seq datasets"
    ),
    iatlas.modules::textBox(
      width = 12,
      p("This module allows you to explore the expression of immunomodulators genes in single-cell RNA-seq datasets.")
    ),
    iatlas.modules::sectionBox(
      title = "Distributions",
      # shiny::column(
      #   width = 6,
        shiny::tagList(
          iatlas.modules::messageBox(
            width = 24,
            shiny::includeMarkdown("inst/markdown/sc_immunomodulators_bubbleplot.markdown")
            # shiny::p("See the percentage of cells from a given type that have counts for selected genes with average expression values.")
          ),
          shiny::verticalLayout(
            iatlas.modules::optionsBox(
              width=24,
              shiny::column(
                width = 4,
                shiny::checkboxGroupInput(
                  ns("datasets"),
                  "Choose dataset(s)",
                  choices = c("Bi 2021 - ccRCC" = "Bi_2021",
                              "Krishna 2021 - ccRCC" = "Krishna_2021",
                              "Li 2022 - ccRCC" = "Li_2022",
                              "HTAN MSK - SCLC" = "MSK",
                              "Shiao 2024 - BRCA" = "Shiao_2024",
                              "HTAN Vanderbilt - colon polyps" = "Vanderbilt"
                  ),
                  selected = c("MSK", "Vanderbilt")
                )
              ),
              shiny::column(
                width = 8,
                shiny::selectizeInput(
                  ns("genes"),
                  label = "Select or Search for genes",
                  choices = NULL,
                  multiple = TRUE
                )
              )
            ),
            iatlas.modules::plotBox(
              width=24,
              plotly::plotlyOutput(ns("bubble_plot"), height = "800px")
            )
          )
        #)
      ),
      # shiny::column(
      #   width = 6,
        iatlas.modules::messageBox(
          width = 12,
          shiny::includeMarkdown("inst/markdown/sc_immunomodulators.markdown"),
          shiny::actionLink(ns("method_link"), "Click to view pseudobulk method description.")
        ),
        sc_immune_features_distribution_ui(ns("sc_immunomodulators_distribution"))
      #)
  )
  )
}
