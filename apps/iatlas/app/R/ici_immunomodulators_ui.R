ici_immunomodulators_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::titleBox(
      "iAtlas Explorer — Immunomodulators Expression in Immune Checkpoint Inhibitors datasets"
    ),
    iatlas.modules::textBox(
      width = 12,
      p("Explore the expression of genes that code for immunomodulating proteins, including checkpoint proteins.")
    ),
    iatlas.modules::sectionBox(
      title = "Distributions",
      iatlas.modules::messageBox(width = 12,
                 shiny::p("Select the datasets of interest, an immunomodulator gene, and a criterion to group samples to see the distribution of gene expression within sample groups.
                          Samples can be further divided into additional sub-group based on the availability of those groups for each dataset.  Use the plot parameters to adjust the type of plot and choice of scale.

                          A table with statistical tests comparing all pairwise combinations of groups, for each dataset, is provided at the bottom of the page. You can view a histogram for any individual distributions by clicking on its violin plot. "
                          )),
      ici_distribution_ui(ns("ici_immunomodulators_distribution"))
        )
      )
}
