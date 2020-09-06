io_targets_ui <- function(id) {

    ns <- shiny::NS(id)

    source_files <- c("R/modules/ui/submodules/module_ui.R")

    for (file in source_files) {
        source(file, local = T)
    }

    shiny::tagList(
        iatlas.app::titleBox("iAtlas Explorer — IO Targets"),
        iatlas.app::textBox(
            width = 12,
            shiny::includeMarkdown("markdown/io_target.markdown")
        ),
        iatlas.app::sectionBox(
            title = "IO Target Gene Expression Distributions",
            module_ui(ns("distributions"))
        ),
        iatlas.app::sectionBox(
            title = "IO Target Annotations",
            module_ui(ns("datatable"))
        )
    )
}
