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
    iatlas.modules::optionsBox(
      width = 12,
      shiny::uiOutput(ns("gene_selection"))
    ),
    iatlas.modules::plotBox(
      width = 12,
      shiny::column(
        width = 9,
        shiny::uiOutput(ns("frequency_plot"))
      ),
      shiny::column(
        width = 3,
        div(
          DT::DTOutput(ns("legend")),
          DT::DTOutput(ns("pmhc_tbl")),
          style = "font-size: 75%"
        )
      )
    )
  )
}
