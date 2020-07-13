cohort_filter_selection_ui <- function(id) {

    ns <- shiny::NS(id)

    source("R/modules/ui/submodules/insert_remove_element_ui.R", local = T)

    shiny::tagList(
        shiny::fluidRow(
            .GlobalEnv$optionsBox(
                width = 12,
                insert_remove_element_ui(
                    ns("tags_filter"),
                    "Add group filter"
                )
            ),
            .GlobalEnv$optionsBox(
                width = 12,
                insert_remove_element_ui(
                    ns("numeric_filter"),
                    "Add numeric filter"
                )
            )
        ),
        .GlobalEnv$tableBox(
            width = 12,
            shiny::textOutput(ns("samples_text"))
        )
    )

}
