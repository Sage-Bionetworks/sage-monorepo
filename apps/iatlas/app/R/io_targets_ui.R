io_targets_ui <- function(id) {

    ns <- shiny::NS(id)

    shiny::tagList(
        titleBox("iAtlas Explorer — IO Targets"),
        textBox(
            width = 12,
            shiny::includeMarkdown("inst/markdown/io_target.markdown")
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
