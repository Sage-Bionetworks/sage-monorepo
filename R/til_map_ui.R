til_map_ui <- function(id) {

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::titleBox("iAtlas Explorer — TIL Maps"),
    iatlas.modules::textBox(
      width = 12,
      shiny::includeMarkdown(get_markdown_path("tilmap"))
    ),
    iatlas.modules::sectionBox(
      title = "Distributions",
      module_ui(ns("til_map_distributions"))
    ),
    iatlas.modules::sectionBox(
      title = "TIL Map Annotations",
      module_ui(ns("til_map_datatable"))
    )
  )
}
