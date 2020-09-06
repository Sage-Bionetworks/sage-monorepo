tumor_microenvironment_type_fractions_ui <- function(id){

    ns <- shiny::NS(id)

    shiny::tagList(
        iatlas.app::messageBox(
            width = 12,
            shiny::includeMarkdown("markdown/cell_type_fractions.markdown")
        ),
        shiny::fluidRow(
            iatlas.app::optionsBox(
                width = 12,
                shiny::selectInput(
                    inputId = ns("fraction_group_choice"),
                    label = "Select Cell Fraction Type",
                    choices = c(
                        "Immune Cell Proportion - Common Lymphoid and Myeloid Cell Derivative Class",
                        "Immune Cell Proportion - Differentiated Lymphoid and Myeloid Cell Derivative Class",
                        "Immune Cell Proportion - Multipotent Progenitor Cell Derivative Class",
                        "Immune Cell Proportion - Original"
                    ),
                    selected = "Immune Cell Proportion - Differentiated Lymphoid and Myeloid Cell Derivative Class"
                )
            )
        ),
        shiny::fluidRow(
            shiny::fluidRow(
                iatlas.app::plotBox(
                    width = 12,
                    "barplot" %>%
                        ns() %>%
                        plotly::plotlyOutput(.) %>%
                        shinycssloaders::withSpinner(.),
                    plotly_ui(ns("barplot"))
                )
            )
        )
    )
}
