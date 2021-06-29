ici_models_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    titleBox(
      "iAtlas Explorer — Multivariable Models with ICI data"
    ),
    textBox(
      width = 12,
      p("Train and run multivariable regression models with cross-validation on ICI genomics and immunogenomics data.")
    ),
    sectionBox(
      title = "Multivariable Models",
      ici_models_main_ui(ns("ici_models_train"))
    )
  )
}
