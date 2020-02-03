overall_cell_proportions_ui <- function(id){

    ns <- shiny::NS(id)

    source("R/modules/ui/submodules/plotly_ui.R", local = T)

    .GlobalEnv$sectionBox(
        title = "Overall Cell Proportions",
        .GlobalEnv$messageBox(
            width = 12,
            shiny::includeMarkdown(
                "markdown/overall_cell_proportions1.markdown"
            )
        ),
        shiny::fluidRow(plotly_ui(ns("plotly_barplot"))),
        .GlobalEnv$messageBox(
            width = 12,
            shiny::includeMarkdown(
                "markdown/overall_cell_proportions2.markdown"
            )
        ),
        shiny::fluidRow(
            plotly_ui(ns("plotly_scatterplot")),
            show_group_text = F
        ),
    )
}
