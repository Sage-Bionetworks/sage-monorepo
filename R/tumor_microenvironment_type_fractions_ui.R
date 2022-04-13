tumor_microenvironment_type_fractions_ui <- function(id){

    ns <- shiny::NS(id)
    iatlas.modules::barplot_ui(
      ns("barplot"),
      barplot_html = shiny::includeMarkdown(
        get_markdown_path("cell_type_fractions")
      ),
    )
}
