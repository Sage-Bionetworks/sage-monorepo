driver_associations_ui <- function(id) {

    ns <- shiny::NS(id)

    source("R/modules/ui/submodules/univariate_driver_ui.R", local = T)
    source("R/modules/ui/submodules/multivariate_driver_ui.R", local = T)

    shiny::tagList(
        .GlobalEnv$titleBox(
            "iAtlas Explorer — Association with Driver Mutations"
        ),
        .GlobalEnv$textBox(
            width = 12,
            shiny::includeMarkdown("markdown/driver.markdown")
        ),
        # univariate_driver_ui(ns("univariate_driver")),
        multivariate_driver_ui(ns("multivariate_driver"))
    )
}

