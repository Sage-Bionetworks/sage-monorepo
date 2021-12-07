io_targets_ui <- function(id) {

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::titleBox("iAtlas Explorer — IO Targets"),
    iatlas.modules::textBox(
      width = 12,
      shiny::includeMarkdown(get_markdown_path("io_target"))
    ),
    iatlas.modules::sectionBox(
      title = "IO Target Gene Expression Distributions",
      module_ui(ns("distributions"))
    ),
    iatlas.modules::sectionBox(
      title = "IO Target Annotations",
      module_ui(ns("datatable"))
    )
  )
}
