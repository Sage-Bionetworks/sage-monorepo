til_map_ui <- function(id) {

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlasModules::titleBox("iAtlas Explorer — TIL Maps"),
    iatlasModules::textBox(
      width = 12,
      shiny::includeMarkdown(get_markdown_path("tilmap"))
    ),
    iatlasModules::sectionBox(
      title = "Distributions",
      module_ui(ns("til_map_distributions"))
    ),
    iatlasModules::sectionBox(
      title = "TIL Map Annotations",
      module_ui(ns("til_map_datatable"))
    )
  )
}
