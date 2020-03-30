til_maps_ui <- function(id) {

    ns <- shiny::NS(id)

    source("R/modules/ui/submodules/til_map_distributions_ui.R", local = T)
    source("R/modules/ui/submodules/til_map_datatable_ui.R", local = T)

    shiny::tagList(
        .GlobalEnv$titleBox("iAtlas Explorer — TIL Maps"),
        .GlobalEnv$textBox(
            width = 12,
            shiny::includeMarkdown("markdown/tilmap.markdown")
        ),

        # Distributions ----
        .GlobalEnv$sectionBox(
            title = "Distributions",
            shiny::conditionalPanel(
                condition = "output.display_til",
                til_map_distributions_ui(ns("til_map_distributions")),
                ns = ns
            ),
            shiny::conditionalPanel(
                condition = "!output.display_til",
                .GlobalEnv$textBox(
                    width = 12,
                    paste0(
                        "Currently selected cohort does not have the needed ",
                        "features to show display this section"
                    )
                ),
                ns = ns
            )
        ),
        # Datatable ----
        shiny::conditionalPanel(
            condition = "output.display_til",
            til_map_datatable_ui(ns("til_map_datatable")),
            ns = ns
        ),
        shiny::conditionalPanel(
            condition = "!output.display_til",
            .GlobalEnv$sectionBox(
                title = "TIL Map Annotations",
                .GlobalEnv$textBox(
                    width = 12,
                    paste0(
                        "Currently selected cohort does not have the needed ",
                        "features to show display this section"
                    )
                )
            ),
            ns = ns
        )
    )
}
