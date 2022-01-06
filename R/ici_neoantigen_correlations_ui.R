ici_neoantigen_correlations_ui <- function(
  id,
  cohort_obj
){
  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::messageBox(
      width = 24,
      p("Explore the correlation of frequency of neoantigen of a selected type with a selected class of immune features.")
    ),
    # iatlas.modules::heatmap_ui(
    #   ns("heatmap")
    # )
    iatlas.modules::optionsBox(
      width = 24,
      shiny::column(
        width = 5,
        shiny::uiOutput(ns("class_selection_ui"))
      ),
      shiny::column(
        width = 4,
        shiny::uiOutput(ns("neoantigen_selection_ui"))
      ),
      shiny::column(
        width = 3,
        shiny::uiOutput(ns("summarise_function_ui"))
      )
    ),
    iatlas.modules::plotBox(
      width = 24,
      plotly::plotlyOutput(ns("plot"), width = "100%", height = "600px")
    )
  )
}
