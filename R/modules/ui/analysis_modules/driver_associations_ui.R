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
        # Univariate ----
        .GlobalEnv$sectionBox(
            title = paste0(
                "Immune Response Association With Driver Mutations ",
                "-- single variable"
            ),
            shiny::conditionalPanel(
                condition = "output.display_ud",
                univariate_driver_ui(ns("univariate_driver")),
                ns = ns
            ),
            shiny::conditionalPanel(
                condition = "!output.display_ud",
                .GlobalEnv$textBox(
                    width = 12,
                    paste0(
                        "Currently selected cohort does not have the needed ",
                        "features to display this section"
                    )
                ),
                ns = ns
            )
        ),
        # Univariate ----
        .GlobalEnv$sectionBox(
            title = paste0(
                "Immune Response Association With Driver Mutations ",
                "-- multivariate"
            ),
            shiny::conditionalPanel(
                condition = "output.display_md",
                multivariate_driver_ui(ns("multivariate_driver")),
                ns = ns
            ),
            shiny::conditionalPanel(
                condition = "!output.display_md",
                .GlobalEnv$textBox(
                    width = 12,
                    paste0(
                        "Currently selected cohort does not have the needed ",
                        "features to display this section"
                    )
                ),
                ns = ns
            )
        )
    )
}

