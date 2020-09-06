driver_associations_ui <- function(id) {

    ns <- shiny::NS(id)

    shiny::tagList(
        iatlas.app::titleBox(
            "iAtlas Explorer — Association with Driver Mutations"
        ),
        iatlas.app::textBox(
            width = 12,
            shiny::includeMarkdown("markdown/driver.markdown")
        ),
        iatlas.app::sectionBox(
            title = paste0(
                "Immune Response Association With Driver Mutations ",
                "-- univariate"
            ),
            module_ui(ns("univariate_driver"))
        ),
        iatlas.app::sectionBox(
            title = paste0(
                "Immune Response Association With Driver Mutations ",
                "-- multivariate"
            ),
            module_ui(ns("multivariate_driver"))
        )
    )
}

