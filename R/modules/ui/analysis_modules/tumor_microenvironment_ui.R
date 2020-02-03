tumor_microenvironment_ui <- function(id) {

    source("R/modules/ui/submodules/overall_cell_proportions_ui.R", local = T)
    source("R/modules/ui/submodules/cell_type_fractions_ui.R", local = T)

    ns <- shiny::NS(id)

    shiny::tagList(
        .GlobalEnv$titleBox("iAtlas Explorer — Tumor Microenvironment"),
        .GlobalEnv$textBox(
            width = 12,
            shiny::includeMarkdown(
                "markdown/tumor_microenvironment.markdown"
            )
        ),
        overall_cell_proportions_ui(ns("overall_cell_proportions")),
        cell_type_fractions_ui(ns("cell_type_fractions"))
    )
}


