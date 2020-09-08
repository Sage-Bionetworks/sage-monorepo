aboutpage_ui <- function(){
  shiny::fluidPage(
    shiny::br(),
    titleBox("About"),
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
