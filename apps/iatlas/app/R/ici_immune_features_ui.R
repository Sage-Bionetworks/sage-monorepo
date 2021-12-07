ici_immune_features_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::titleBox(
      "iAtlas Explorer — Immune Features in Immune Checkpoint Inhibitors datasets"
    ),
    iatlas.modules::textBox(
      width = 12,
      p("This module allows you to see how immune readouts vary across your groups.")
      ),
    iatlas.modules::sectionBox(
      title = "Distributions",
      iatlas.modules::messageBox(width = 12,
                 shiny::p("This displays the value of immune readouts by sample group.
      Select the datasets of interest, a variable of interest, and how you want to split your samples into groups.
      Samples can be further divided into additional sub-group based on the availability of those groups for each dataset.
      Use the plot parameters to adjust the type of plot and choice of scale.

      A table with the result of statistical tests comparing all pairwise combinations of groups is provided at the bottom of
      the page. For an A vs B comparison, a positive t-statistics or Wilcox statistics corresponds to an elevated value in the A group over group B
      (Mean value in A greater than mean in B). The table also includes a comparison of the log2 fold change of groups." )),
      ici_distribution_ui(ns("ici_immune_features_distribution"))
    )
  )
}
