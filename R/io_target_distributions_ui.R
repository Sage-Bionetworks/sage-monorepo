io_target_distributions_ui <- function(id) {
  ns <- shiny::NS(id)
  iatlasModules::distributions_plot_ui(
    ns("distplot"),
    html = shiny::includeMarkdown(
      get_markdown_path("io_target_dist")
    )
  )
}
