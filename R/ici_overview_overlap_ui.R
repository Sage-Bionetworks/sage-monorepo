ici_overview_overlap_ui <- function(id){
  ns <- shiny::NS(id)

  shiny::tagList(
    messageBox(
      width = 24,
      shiny::p("See a mosaic plot for two sample groups.")
    ),
    optionsBox(
      width = 12,
      shiny::uiOutput(ns("select_group2"))
    ),
    plotBox(
      width = 12,
      plotly::plotlyOutput(ns("io_mosaic"), height = "600px") %>%
        shinycssloaders::withSpinner()
    )
  )
}
