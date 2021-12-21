ici_neoantigen_classes_ui <- function(
  id,
  cohort_obj
){
  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::messageBox(
      width = 24,
      p("Neonatigens can have differente sources. Here you can explore the frequency of neoantigens for each class.")
    ),
    # iatlas.modules::optionsBox(
    #   width = 12,
    #   shiny::checkboxInput(ns("group_by_patient"),
    #                      "Show frequency for patient level")
    # ),
    iatlas.modules::plotBox(
      width = 12,
      plotly::plotlyOutput(ns("neoantigen_classes_plot"))
    )
  )
}
