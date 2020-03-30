tumor_microenvironment_ui <- function(id) {

    source("R/modules/ui/submodules/overall_cell_proportions_ui.R", local = T)
    source("R/modules/ui/submodules/cell_type_fractions_ui.R", local = T)

    ns <- shiny::NS(id)

    shiny::tagList(
        .GlobalEnv$titleBox("iAtlas Explorer — Tumor Microenvironment"),
        .GlobalEnv$textBox(
            width = 12,
            shiny::includeMarkdown(
                "markdown/tumor_microenvironment.markdown"
            )
        ),
        # Overall Cell Proportions ----
        shiny::conditionalPanel(
            condition = "output.display_ocp",
            overall_cell_proportions_ui(ns("overall_cell_proportions")),
            ns = ns
        ),
        shiny::conditionalPanel(
            condition = "!output.display_ocp",
            .GlobalEnv$sectionBox(
                title = "Overall Cell Proportions",
                .GlobalEnv$textBox(
                    width = 12,
                    paste0(
                        "Currently selected cohort does not have the needed",
                        "features to show display this plot"
                    )
                )
            ),
            ns = ns
        ),
        # Cell Type Fractions ----
        shiny::conditionalPanel(
            condition = "output.display_ctf",
            cell_type_fractions_ui(ns("cell_type_fractions")),
            ns = ns
        ),
        shiny::conditionalPanel(
            condition = "!output.display_ctf",
            .GlobalEnv$sectionBox(
                title = "Cell Type Fractions",
                .GlobalEnv$textBox(
                    width = 12,
                    paste0(
                        "Currently selected cohort does not have the needed",
                        "features to show display this plot"
                    )
                )
            ),
            ns = ns
        ),
    )
}


