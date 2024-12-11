ici_neoantigen_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::titleBox(
      "iAtlas Explorer — Neoantigens in Immune Checkpoint Inhibitors datasets"
    ),
    iatlas.modules::textBox(
      width = 12,
      p("Explore the prevalence, variety and distribution of predicted neoantigens in iAtlas datasets. Neoantigen prediction was performed using the Landscape of Effective Neoantigens Software (LENS) workflow",
      a(href="https://academic.oup.com/bioinformatics/article/39/6/btad322/7162685", "(Vensko 2023).")),
      p("Check out our ",
        a(href="https://docs.google.com/presentation/d/1yQ1_twHL33bMEDRlsxf-Thz589uafOvrB8alMfOgzdE/edit?usp=sharing", "tutorial"),
        "and", a(href="https://github.com/CRI-iAtlas/iatlas-notebooks/blob/main/ici_neoantigen.ipynb", "Jupyter notebook"),
        "reproducing the code run in this module."),
      shiny::actionLink(ns("method_link"), "Click to view the description of the method for neoantigen prediction.")
    ),
    iatlas.modules::messageBox(
      width = 12,
      shiny::htmlOutput(ns("excluded_dataset"))
    ),
    iatlas.modules::sectionBox(
      title = "Classes of Neoantigens",
      ici_neoantigen_classes_ui(ns("ici_neoantigen_classes")),
    ),
    iatlas.modules::sectionBox(
      title = "Correlations",
      ici_neoantigen_correlations_ui(ns("ici_neoantigen_correlations"))
    ),
    iatlas.modules::sectionBox(
      title = "Frequency of peptides",
      ici_neoantigen_frequency_ui(ns("ici_neoantigen_frequency"))
    ),
  )
}
