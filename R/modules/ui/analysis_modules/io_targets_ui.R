io_targets_ui <- function(id) {

    ns <- shiny::NS(id)

    source_files <- c(
        "R/modules/ui/submodules/io_target_distributions_ui.R",
        "R/modules/ui/submodules/io_target_datatable_ui.R",
        "R/modules/ui/submodules/call_module_ui.R"
    )

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
            call_module_ui(
                ns("distributions"),
                io_target_distributions_ui
            )
        ),
        iatlas.app::sectionBox(
            title = "IO Target Annotations",
            call_module_ui(
                ns("datatable"),
                io_target_datatable_ui
            )
        )
    )
}
