tumor_microenvironment_ui <- function(id) {

    source("R/modules/ui/submodules/overall_cell_proportions_ui.R", local = T)
    source("R/modules/ui/submodules/cell_type_fractions_ui.R", local = T)
    source("R/modules/ui/submodules/call_module_ui.R", local = T)

    ns <- shiny::NS(id)

    shiny::tagList(
        .GlobalEnv$titleBox("iAtlas Explorer — Tumor Microenvironment"),
        .GlobalEnv$textBox(
            width = 12,
            shiny::includeMarkdown(
                "markdown/tumor_microenvironment.markdown"
            )
        ),
        .GlobalEnv$sectionBox(
            title = "Overall Cell Proportions",
            call_module_ui(
                ns("overall_cell_proportions"),
                overall_cell_proportions_ui
            )
        ),
        .GlobalEnv$sectionBox(
            title = "Cell Type Fractions",
            call_module_ui(
                ns("cell_type_fractions"),
                cell_type_fractions_ui
            )
        )
    )
}


