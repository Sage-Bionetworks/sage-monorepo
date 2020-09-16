io_targets_ui <- function(id) {

  ns <- shiny::NS(id)

  shiny::tagList(
    titleBox("iAtlas Explorer — IO Targets"),
    textBox(
      width = 12,
      shiny::includeMarkdown(get_markdown_path("io_target"))
    ),
    sectionBox(
      title = "IO Target Gene Expression Distributions",
      module_ui(ns("distributions"))
    ),
    sectionBox(
      title = "IO Target Annotations",
      module_ui(ns("datatable"))
    )
  )
}
