immunomodulators_distributions_ui <- function(id) {
  ns <- shiny::NS(id)
  iatlasModules::distributions_plot_ui(
    ns("distplot"),
    html = shiny::includeMarkdown(
      get_markdown_path("immunomodulators_distributions")
    )
  )
}
