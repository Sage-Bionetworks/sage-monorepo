immune_feature_distributions_ui <- function(id) {
    ns <- shiny::NS(id)
    iatlas.modules::distributions_plot_ui(
      ns("distplot"),
      html = shiny::includeMarkdown(
        get_markdown_path("immune_features_dist")
      )
    )
}
