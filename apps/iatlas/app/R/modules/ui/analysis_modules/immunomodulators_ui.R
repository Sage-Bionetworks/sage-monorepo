immunomodulators_ui <- function(id) {

    ns <- shiny::NS(id)

    source_files <- c(
        "R/modules/ui/submodules/immunomodulator_distributions_ui.R",
        "R/modules/ui/submodules/immunomodulator_datatable_ui.R",
        "R/modules/ui/submodules/call_module_ui.R"
    )

    for (file in source_files) {
        source(file, local = T)
    }

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
            call_module_ui(
                ns("distributions"),
                immunomodulators_distributions_ui
            )
        ),
        iatlas.app::sectionBox(
            title = "Immunomodulator Annotations",
            call_module_ui(
                ns("datatable"),
                immunomodulators_datatable_ui
            )
        )
    )
}
