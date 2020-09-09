cohort_filter_selection_ui <- function(id) {

    ns <- shiny::NS(id)

    shiny::tagList(
        shiny::fluidRow(
            optionsBox(
                width = 12,
                insert_remove_element_ui(
                    ns("tags_filter"),
                    "Add group filter"
                )
            ),
            optionsBox(
                width = 12,
                insert_remove_element_ui(
                    ns("numeric_filter"),
                    "Add numeric filter"
                )
            )
        ),
        tableBox(
            width = 12,
            shiny::textOutput(ns("samples_text"))
        )
    )

}
