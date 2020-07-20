resourcespage <- shiny::fluidPage(
    shiny::br(),
    iatlas.app::titleBox("Resources"),
    shiny::fluidRow(
        shiny::column(
            width = 12,
            shiny::column(
                width = 12,
                shiny::includeMarkdown("markdown/resources.markdown")
            )
        )
    )
)
