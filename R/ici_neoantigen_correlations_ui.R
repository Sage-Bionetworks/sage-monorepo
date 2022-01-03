ici_neoantigen_correlations_ui <- function(
  id,
  cohort_obj
){
  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::messageBox(
      width = 24,
      p("Explore the correlation od frequency of neoantigen of a selected type with a selected class of immune features.")
    ),
    iatlas.modules::heatmap_ui(
      ns("heatmap")
    )
  )
}
