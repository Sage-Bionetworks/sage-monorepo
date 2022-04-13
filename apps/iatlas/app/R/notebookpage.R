notebookpage_ui <- function(){
  shiny::fluidPage(
    shiny::br(),
    iatlas.modules::titleBox("iAtlas Notebooks"),
    shiny::fluidRow(
      shiny::column(
        width = 12,
        shiny::column(
          width = 12,
          shiny::includeMarkdown("inst/markdown/notebook.markdown")
        )
      )
    )
  )
}
