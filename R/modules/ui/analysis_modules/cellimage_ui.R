cellimage_ui <- function(id){

    ns <- shiny::NS(id)

    source_files <- c(
        "R/modules/ui/submodules/cellimage_main_ui.R",
        "R/modules/ui/submodules/call_module_ui.R"
    )

    for (file in source_files) {
        source(file, local = T)
    }

    shiny::tagList(
        iatlas.app::titleBox(
            "iAtlas Explorer — Cell-Interaction Diagram"
        ),
        iatlas.app::textBox(
            width = 12,
            "This module allows you to depict the estimated abundance of tumor cells and representative innate and adaptive cells in the microenvironment, along with the abundance of receptor and ligands mediating interactions between those cells."
        ),
        iatlas.app::sectionBox(
            title = "Cell-Interaction Diagram",
            call_module_ui(
                ns("cellimage_main"),
                cellimage_main_ui
            )
        )
    )
}
