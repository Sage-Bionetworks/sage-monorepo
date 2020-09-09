immunomodulators_ui <- function(id) {

    ns <- shiny::NS(id)

    shiny::tagList(
        titleBox("iAtlas Explorer — Immunomodulators"),
        textBox(
            width = 12,
            shiny::p(paste0(
                "Explore the expression of genes that code for ",
                "immunomodulating proteins, including checkpoint proteins."
            )),
        ),
        sectionBox(
            title = "Immunomodulator Distributions",
            module_ui(ns("distributions"))
        ),
        sectionBox(
            title = "Immunomodulator Annotations",
            module_ui(ns("datatable")
                      )
        )
    )
}
