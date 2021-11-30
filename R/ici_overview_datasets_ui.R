ici_overview_datasets_ui <- function(id){
  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::messageBox(
      width = 24,
      shiny::includeMarkdown(get_markdown_path(
        "ici_overview_datasets"
      ))
    ),
    iatlas.modules::plotBox(
      width = 12,
      DT::DTOutput(
        ns("ici_datasets_df")
      ),
      shiny::br(),
      tags$a(href="https://www.synapse.org/#!Synapse:syn24200710", "Download data from Synapse")
    )
  )
}
