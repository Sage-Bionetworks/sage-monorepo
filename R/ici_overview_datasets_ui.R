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
      shiny::downloadButton(ns('download_metadata'), 'Download Dataset Metadata'),
      shiny::downloadButton(ns('download_data'), 'Download Immune Features and Clinical data'),
      shiny::downloadButton(ns('download_expr'), 'Download Gene Expression data'),
      shiny::br(),
      tags$a(href="https://www.synapse.org/#!Synapse:syn24200710", "Download full gene expression data from Synapse")
    )
  )
}
