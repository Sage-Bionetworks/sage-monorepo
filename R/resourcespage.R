resourcespage_ui <- function(){
  shiny::fluidPage(
    shiny::br(),
    iatlas.modules::titleBox("Resources"),
    shiny::fluidRow(
      shiny::column(
        width = 12,
        shiny::column(
          width = 12,
          shiny::includeMarkdown("inst/markdown/resources.markdown")
        )
      )
    )
  )
}
