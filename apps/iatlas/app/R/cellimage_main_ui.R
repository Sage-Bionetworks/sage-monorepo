cellimage_main_ui <- function(id){

  ns <- shiny::NS(id)
  shiny::tagList(
    messageBox(
      width = 12,
      shiny::includeMarkdown(get_markdown_path("cellimage")),
      shiny::actionLink(ns("method_link"), "Click to view method.")
    ),
    shiny::fluidRow(
      optionsBox(
        shiny::column(
          width = 6,
          shiny::radioButtons(
            ns("ui1"),
            "Select type of visualization:",
            choices = c("Illustration", "Network"),
            selected = "Illustration"
          )
        ),
        shiny::column(
          width = 6,
          shiny::uiOutput(ns("select_ui"))
        )
      ),
      optionsBox(
        shiny::column(
          width = 6,
          shiny::radioButtons(
            ns("ui2"),
            "Select type of visualization:",
            choices = c("Illustration", "Network"),
            selected = "Network"
          )
        ),
        shiny::column(
          width = 6,
          shiny::uiOutput(ns("select_ui2"))
        )
      )
    ),
    shiny::fluidRow(
      plotBox(
        width = 6,
        shiny::uiOutput(ns("plot1")) %>%
          shinycssloaders::withSpinner(.)
      ),
      plotBox(
        width = 6,
        shiny::uiOutput(ns("plot2")) %>%
          shinycssloaders::withSpinner(.)
      )
    ),
    shiny::img(src = "images/cell-image-legend.png", width = "100%"),
    shiny::br(),
    shiny::actionButton(ns("methodButton"), "Click to view method")
  )
}
