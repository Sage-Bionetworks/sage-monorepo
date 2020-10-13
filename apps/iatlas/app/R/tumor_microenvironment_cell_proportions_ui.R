tumor_microenvironment_cell_proportions_ui <- function(id){
  ns <- shiny::NS(id)
  iatlas.modules::barplot_ui(
    ns("barplot"),
    barplot_html = shiny::includeMarkdown(
      get_markdown_path("overall_cell_proportions1")
    ),
    drilldown_html = shiny::includeMarkdown(
      get_markdown_path("overall_cell_proportions2")
    )
  )
}

