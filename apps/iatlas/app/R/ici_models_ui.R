ici_models_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::titleBox(
      "iAtlas Explorer — Machine Learning Models of Response to Immunotherapy with Immune Checkpoint Inhibition datasets"
    ),
    iatlas.modules::textBox(
      width = 12,
      p("Train and run multivariable models with cross-validation on ICI genomics and immunogenomics data.")
    ),
    iatlas.modules::sectionBox(
      title = "Multivariable Models",
      ici_models_main_ui(ns("ici_models_train"))
    )
  )
}
