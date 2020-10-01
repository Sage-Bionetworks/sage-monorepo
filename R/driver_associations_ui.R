driver_associations_ui <- function(id) {

  ns <- shiny::NS(id)

  shiny::tagList(
    titleBox(
      "iAtlas Explorer — Association with Driver Mutations"
    ),
    textBox(
      width = 12,
      shiny::includeMarkdown(get_markdown_path("driver"))
    ),
    # sectionBox(
    #   title = paste0(
    #     "Immune Response Association With Driver Mutations ",
    #     "-- univariate"
    #   ),
    #   module_ui(ns("univariate_driver"))
    # ),
    sectionBox(
      title = paste0(
        "Immune Response Association With Driver Mutations ",
        "-- multivariate"
      ),
      module_ui(ns("multivariate_driver"))
    )
  )
}

