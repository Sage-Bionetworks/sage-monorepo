cellimage_plot_ui <- function(id){

  ns <- shiny::NS(id)
  shiny::tagList(
    plotBox(
      width = 6,
      shiny::plotOutput(ns("plot"), height = 600)  %>%
        shinycssloaders::withSpinner(.)
    )
  )
}
