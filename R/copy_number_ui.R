copy_number_ui <- function(id){

    ns <- shiny::NS(id)

    shiny::tagList(
        titleBox(
            "iAtlas Explorer — Association with Copy Number Variations"
        ),
        textBox(
            width = 12,
            paste0(
                "Explore statistical associations between immune readouts ",
                "and copy number variations."
            )
        ),
        sectionBox(
            title = "Immune Response Association With Copy Number Variation",
            module_ui(ns("copy_number_response"))
        )
    )
}
