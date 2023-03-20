sc_umap_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::messageBox(
      width = 24,
      shiny::p("UMAP")
    ),
    shiny::fluidRow(
      shiny::column(
        width = 3,
        iatlas.modules::optionsBox(
          width=24,
          shiny::uiOutput(ns("select_color_criteria"))
        )
      ),
      shiny::column(
        width = 9,
        iatlas.modules::plotBox(
          width=24,
          plotly::plotlyOutput(ns("umap_plot"))
        )
      )
    )
  )
}
