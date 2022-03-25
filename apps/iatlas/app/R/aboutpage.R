aboutpage_ui <- function(){
  shiny::fluidPage(
    shiny::br(),
    iatlasModules::titleBox("About"),
    shiny::fluidRow(
      shiny::column(
        width = 12,
        shiny::column(
          width = 12,
          shiny::includeMarkdown("inst/markdown/about.markdown")
        )
      )
    )
  )
}
