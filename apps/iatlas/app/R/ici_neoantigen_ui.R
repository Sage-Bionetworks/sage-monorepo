ici_neoantigen_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::titleBox(
      "iAtlas Explorer — Neoantigens in Immune Checkpoint Inhibitors datasets"
    ),
    iatlas.modules::textBox(
      width = 12,
      p("Explore the prevalence, variety and distribution of predicted neoantigens in iAtlas datasets."),
      shiny::actionLink(ns("method_link"), "Click to view neoantigen prediction method description.")
    ),
    iatlas.modules::messageBox(
      width = 12,
      shiny::htmlOutput(ns("excluded_dataset"))
    ),
    iatlas.modules::sectionBox(
      title = "Classes of Neoantigens",
      ici_neoantigen_classes_ui(ns("ici_neoantigen_classes"))
    ),
    iatlas.modules::sectionBox(
      title = "Frequency of peptides",
      ici_neoantigen_frequency_ui(ns("ici_neoantigen_frequency"))
    ),
    iatlas.modules::sectionBox(
      title = "Correlations",
      ici_neoantigen_correlations_ui(ns("ici_neoantigen_correlations"))
    )
  )
}
