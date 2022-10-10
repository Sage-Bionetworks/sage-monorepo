sc_bubbleplot_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::messageBox(
      width = 24,
      shiny::p("See the percentage of cells from a given type that have counts for selected genes with average expression values.")
    ),
    shiny::fluidRow(
      shiny::column(
        width = 3,
        iatlas.modules::optionsBox(
          width=24,
          shiny::uiOutput(ns("select_cells")),
          shiny::uiOutput(ns("select_genes"))
        )
      ),
      shiny::column(
        width = 9,
        iatlas.modules::plotBox(
          width=24,
          plotly::plotlyOutput(ns("bubble_plot"))
        )
      )
    )
  )
}
