driver_associations_ui <- function(id) {

    ns <- shiny::NS(id)

    source("R/modules/ui/submodules/univariate_driver_ui.R", local = T)
    source("R/modules/ui/submodules/call_module_ui.R", local = T)
    source("R/modules/ui/submodules/multivariate_driver_ui.R", local = T)

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
                "-- single variable"
            ),
            call_module_ui(
                ns("univariate_driver"),
                univariate_driver_ui
            )
        ),
        iatlas.app::sectionBox(
            title = paste0(
                "Immune Response Association With Driver Mutations ",
                "-- multivariate"
            ),
            call_module_ui(
                ns("multivariate_driver"),
                multivariate_driver_ui
            )
        )
    )
}

