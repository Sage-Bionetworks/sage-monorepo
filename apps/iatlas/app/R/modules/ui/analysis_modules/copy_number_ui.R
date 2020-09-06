copy_number_ui <- function(id){

    ns <- shiny::NS(id)

    source_files <- c(
        "R/modules/ui/submodules/module_ui.R"
    )

    for (file in source_files) {
        source(file, local = T)
    }

    shiny::tagList(
        iatlas.app::titleBox(
            "iAtlas Explorer — Association with Copy Number Variations"
        ),
        iatlas.app::textBox(
            width = 12,
            paste0(
                "Explore statistical associations between immune readouts ",
                "and copy number variations."
            )
        ),
        iatlas.app::sectionBox(
            title = "Immune Response Association With Copy Number Variation",
            module_ui(ns("copy_number_response"))
        )
    )
}
