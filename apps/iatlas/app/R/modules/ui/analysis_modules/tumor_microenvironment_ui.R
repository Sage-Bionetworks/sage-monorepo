tumor_microenvironment_ui <- function(id) {

    ns <- shiny::NS(id)

    shiny::tagList(
        iatlas.app::titleBox("iAtlas Explorer — Tumor Microenvironment"),
        iatlas.app::textBox(
            width = 12,
            shiny::includeMarkdown("markdown/tumor_microenvironment.markdown")
        ),
        iatlas.app::sectionBox(
            title = "Overall Cell Proportions",
            module_ui(ns("tumor_microenvironment_cell_proportions"))
        ),
        iatlas.app::sectionBox(
            title = "Cell Type Fractions",
            module_ui(ns("tumor_microenvironment_type_fractions"))
        )
    )
}


