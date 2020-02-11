docspage <- shiny::fluidPage(
    shiny::br(),
    .GlobalEnv$titleBox("Documentation"),
    .GlobalEnv$messageBox(
        width = 12,
        shiny::includeMarkdown("markdown/docs.markdown")
    ),
    shiny::fluidRow(
        shiny::column(
            width = 12,
            shiny::column(
                width = 12,
                shiny::includeMarkdown("README.md")
            )
        )
    )
)
