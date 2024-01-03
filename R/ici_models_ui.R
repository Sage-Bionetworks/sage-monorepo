ici_models_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::titleBox(
      "CRI iAtlas Explorer — Machine Learning Models of Response to Immunotherapy with Immune Checkpoint Inhibition datasets"
    ),
    iatlas.modules::textBox(
      width = 12,
      p("Train and run multivariable models with cross-validation on ICI genomics and immunogenomics data."),
      p("Check out our ",
      a(href="https://docs.google.com/presentation/d/1Z86yWIQ33KNhQY6PHziSLyjEi1xzLzBPdQLXTWc6bZE/edit#slide=id.p", "tutorial"),
      "and the", a(href="https://github.com/CRI-iAtlas/iatlas-notebooks/tree/main/ici_models_notebooks", "Jupyter notebooks"),
      "reproducing the code run in this module.")
    ),
    iatlas.modules::sectionBox(
      title = "Multivariable Models",
      ici_models_main_ui(ns("ici_models_train"))
    )
  )
}
