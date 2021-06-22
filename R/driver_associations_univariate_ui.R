univariate_driver_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::messageBox(
      width = 12,
      shiny::includeMarkdown(get_markdown_path("driver_single"))
    ),
    shiny::fluidRow(
      iatlas.modules::optionsBox(
        width = 12,
        shiny::column(
          width = 4,
          shiny::uiOutput(ns("response_option_ui"))
        ),
        shiny::column(
          width = 4,
          shiny::numericInput(
            ns("min_mut"),
            "Minimum number of mutant samples per group:",
            2,
            min = 2
          )
        ),
        shiny::column(
          width = 4,
          shiny::numericInput(
            ns("min_wt"),
            "Minimum number of wild type samples per group:",
            2,
            min = 2
          )
        )
      )
    ),
    shiny::fluidRow(
      iatlas.modules::messageBox(
        width = 12,
        shiny::p(shiny::textOutput(ns("result_text")))
      )
    ),
    shiny::fluidRow(
      iatlas.modules::plotBox(
        width = 12,
        "volcano_plot" %>%
          ns() %>%
          plotly::plotlyOutput(.) %>%
          shinycssloaders::withSpinner(.),
        plotly_ui(ns("volcano_plot"))
      )
    ),
    shiny::fluidRow(
      iatlas.modules::plotBox(
        width = 12,
        "violin_plot" %>%
          ns() %>%
          plotly::plotlyOutput(.) %>%
          shinycssloaders::withSpinner(.),
        plotly_ui(ns("violin_plot"))
      )
    )
  )
}
