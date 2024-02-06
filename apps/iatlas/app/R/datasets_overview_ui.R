datasets_overview_ui <-  function(
    id,
    cohort_obj
){
  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::messageBox(
      width = 24,
      p("There are different types of data in CRI iAtlas, select the one you are interested.")
    ),
    iatlas.modules::optionsBox(
      width = 12,
      shiny::radioButtons(
        ns("data_group"),
        "Select the type of data for more information",
        choices = c(
          "ici",
          "cancer_genomics",
          "scrnaseq"
        ),
        selected = "ici"
      )
    ),
    iatlas.modules::plotBox(
      width = 12,
      shiny::conditionalPanel(
        condition = "input.data_group == 'ici'",
        ici_overview_ui(ns("ici_overview")),
        ns = ns
      )
    )

  )
}
