io_targets_ui <- function(id) {

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlasModules::titleBox("iAtlas Explorer — IO Targets"),
    iatlasModules::textBox(
      width = 12,
      shiny::includeMarkdown(get_markdown_path("io_target"))
    ),
    iatlasModules::sectionBox(
      title = "IO Target Gene Expression Distributions",
      module_ui(ns("distributions"))
    ),
    iatlasModules::sectionBox(
      title = "IO Target Annotations",
      module_ui(ns("datatable"))
    )
  )
}
