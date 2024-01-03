ici_neoantigen_classes_ui <- function(
  id,
  cohort_obj
){
  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::messageBox(
      width = 24,
      shiny::p("The bar plots below summarize the frequency of classes of neoantigen for each dataset and group."),
      shiny::actionLink(ns("method_link"), "Click to view the description about classes of neoantigens.")
    ),
  iatlas.modules::plotBox(
    width = 12,
    shiny::column(
      width = 10,
      shiny::uiOutput(ns("classes_plot")) %>%
        shinycssloaders::withSpinner(.)
    ),
    shiny::column(
      width = 2,
      div(
        DT::DTOutput(ns("legend")),
        style = "font-size: 70%"
        )
      )
    )
  )
}
