immune_features_ui <- function(id) {

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::titleBox("CRI iAtlas Explorer — Immune Feature Trends"),
    iatlas.modules::textBox(
      width = 12,
      shiny::p(paste0(
        "This module allows you to see how immune readouts vary ",
        "across your groups, and how they relate to one another."
      )),
      shiny::p("Check out our ",
               a(href="https://github.com/CRI-iAtlas/iatlas-notebooks/blob/main/immune_feature_trends.ipynb", "Jupyter notebook"),
               "reproducing the code run in this module.")
    ),
    iatlas.modules::sectionBox(
      title = "Distributions",
      module_ui(ns("immune_feature_distributions"))
    ),
    iatlas.modules::sectionBox(
      title = "Correlations",
      module_ui(ns("immune_feature_correlations"))
    )
  )
}
