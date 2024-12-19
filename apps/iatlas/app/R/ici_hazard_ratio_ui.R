ici_hazard_ratio_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::titleBox(
      "CRI iAtlas Explorer — Cox Proportional Hazard Ratio to Immune Checkpoint Inhibitors"
    ),
    iatlas.modules::textBox(
      width = 12,
      p("Analyze outcome data in Immune Checkpoint Inhibition datasets using Cox proportional hazard models."),
      p("Check out our ",
        a(href="https://docs.google.com/presentation/d/1KduXxUAb6lKAGDFL74dWiQb7bx2vDW7x48VFXqm_XJ0/edit?usp=sharing", "tutorial"),
        "and the", a(href="https://github.com/CRI-iAtlas/iatlas-notebooks/blob/main/ici_hazard_ratio.ipynb", "Jupyter notebook"),
        "reproducing the code run in this module.")
       ),
    iatlas.modules::sectionBox(
      title = "Cox Proportional Hazard Ratio",
      ici_hazard_ratio_main_ui(ns("ici_hazard_ratio_main"))
    )
  )
}
