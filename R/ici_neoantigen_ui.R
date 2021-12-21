ici_neoantigen_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::titleBox(
      "iAtlas Explorer — Neoantigens in Immune Checkpoint Inhibitors datasets"
    ),
    iatlas.modules::textBox(
      width = 12,
      p("Plot survival curves based on immune characteristics and identify variables associated with outcome.")
    ),
    iatlas.modules::sectionBox(
      title = "Classes of Neoantigens",
      ici_neoantigen_classes_ui(ns("ici_neoantigen_classes"))
    )
  )
}
