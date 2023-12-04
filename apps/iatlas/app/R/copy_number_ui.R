copy_number_ui <- function(id){

    ns <- shiny::NS(id)

    shiny::tagList(
        iatlas.modules::titleBox(
            "iAtlas Explorer — Association with Copy Number Variations"
        ),
        iatlas.modules::textBox(
            width = 12,
            shiny::p("Explore statistical associations between immune readouts and copy number variations."),
            shiny::p( "Please note: As this module relies heavily on pre-computation, it is not currently compatible with user-defined custom sample groups in Cohort Selection."
            )
        ),
        iatlas.modules::sectionBox(
            title = "Immune Response Association With Copy Number Variation",
            module_ui(ns("copy_number_response"))
        )
    )
}
