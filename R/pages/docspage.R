docspage <- shiny::fluidPage(
    shiny::br(),
    iatlas.app::titleBox("Documentation"),
    iatlas.app::messageBox(
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
