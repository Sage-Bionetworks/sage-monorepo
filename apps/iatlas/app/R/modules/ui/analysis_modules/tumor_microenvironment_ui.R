tumor_microenvironment_ui <- function(id) {

    source("R/modules/ui/submodules/tumor_microenvironment_cell_proportions_ui.R", local = T)
    source("R/modules/ui/submodules/tumor_microenvironment_type_fractions_ui.R", local = T)
    source("R/modules/ui/submodules/call_module_ui.R", local = T)

    ns <- shiny::NS(id)

    shiny::tagList(
        iatlas.app::titleBox("iAtlas Explorer — Tumor Microenvironment"),
        iatlas.app::textBox(
            width = 12,
            shiny::includeMarkdown(
                "markdown/tumor_microenvironment.markdown"
            )
        ),
        iatlas.app::sectionBox(
            title = "Overall Cell Proportions",
            call_module_ui(
                ns("tumor_microenvironment_cell_proportions"),
                tumor_microenvironment_cell_proportions_ui
            )
        ),
        iatlas.app::sectionBox(
            title = "Cell Type Fractions",
            call_module_ui(
                ns("tumor_microenvironment_type_fractions"),
                tumor_microenvironment_type_fractions_ui
            )
        )
    )
}


