ici_neoantigen_frequency_ui <- function(
  id,
  cohort_obj
){
  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::messageBox(
      width = 24,
      p("The scatterplot shows the frequency of the top 1% most common combination of MHC, peptide, and gene and how many patients have this given combination.")
    ),
    iatlas.modules::plotBox(
      width = 12,
      plotly::plotlyOutput(ns("neoantigen_frequency_plot"))
    )
  )
}
