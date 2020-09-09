docspage_ui <- function(){
  shiny::fluidPage(
    shiny::br(),
    titleBox("Documentation"),
    messageBox(
      width = 12,
      shiny::includeMarkdown("inst/markdown/docs.markdown")
    ),
    shiny::fluidRow(
      shiny::column(
        width = 12,
        shiny::column(
          width = 12,
          shiny::includeMarkdown("README.md")
        )
      )
    )
  )
}
