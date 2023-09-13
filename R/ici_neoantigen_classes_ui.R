ici_neoantigen_classes_ui <- function(
  id,
  cohort_obj
){
  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::messageBox(
      width = 24,
      p("The bar plots below summarize the frequency of classes of neoantigen for each dataset and group.")
    ),
    iatlas.modules::plotBox(
      width = 12,
      shiny::uiOutput(ns("classes_plot")) %>%
        shinycssloaders::withSpinner(.)
    )
  )
}
