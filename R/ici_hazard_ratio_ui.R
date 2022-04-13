ici_hazard_ratio_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::titleBox(
      "iAtlas Explorer — Cox Proportional Hazard Ratio to Immune Checkpoint Inhibitors"
    ),
    iatlas.modules::textBox(
      width = 12,
      p("Analyze outcome data in Immune Checkpoint Inhibition datasets using Cox proportional hazard models.")
       ),
    iatlas.modules::sectionBox(
      title = "Cox Proportional Hazard Ratio",
      ici_hazard_ratio_main_ui(ns("ici_hazard_ratio_main"))
    )
  )
}
