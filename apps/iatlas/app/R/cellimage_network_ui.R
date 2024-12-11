cellimage_network_ui <- function(id){

  ns <- shiny::NS(id)
  shiny::tagList(
    iatlas.modules::plotBox(
      width = 6,
      cyjShiny::cyjShinyOutput(ns("network"), height = 600) %>%
        shinycssloaders::withSpinner(.)
    )
  )
}
