aboutpage <- shiny::fluidPage(
    shiny::br(),
    .GlobalEnv$titleBox("About"),
    shiny::fluidRow(
        shiny::column(
            width = 12,
            shiny::column(
                width = 12,
                shiny::includeMarkdown("markdown/about.markdown")
            )
        )
    )
)
