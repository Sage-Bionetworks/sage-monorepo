immunomodulators_ui <- function(id) {

    ns <- shiny::NS(id)

    shiny::tagList(
        iatlas.app::titleBox("iAtlas Explorer — Immunomodulators"),
        iatlas.app::textBox(
            width = 12,
            shiny::p(paste0(
                "Explore the expression of genes that code for ",
                "immunomodulating proteins, including checkpoint proteins."
            )),
        ),
        iatlas.app::sectionBox(
            title = "Immunomodulator Distributions",
            module_ui(ns("distributions"))
        ),
        iatlas.app::sectionBox(
            title = "Immunomodulator Annotations",
            module_ui(ns("datatable")
                      )
        )
    )
}
