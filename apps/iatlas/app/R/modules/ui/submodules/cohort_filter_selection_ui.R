cohort_filter_selection_ui <- function(id) {

    ns <- shiny::NS(id)

    source("R/modules/ui/submodules/insert_remove_element_ui.R", local = T)

    shiny::tagList(
        shiny::fluidRow(
            iatlas.app::optionsBox(
                width = 12,
                insert_remove_element_ui(
                    ns("tags_filter"),
                    "Add group filter"
                )
            ),
            iatlas.app::optionsBox(
                width = 12,
                insert_remove_element_ui(
                    ns("numeric_filter"),
                    "Add numeric filter"
                )
            )
        ),
        iatlas.app::tableBox(
            width = 12,
            shiny::textOutput(ns("samples_text"))
        )
    )

}
