multivariate_driver_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::messageBox(
      width = 12,
      shiny::includeMarkdown(get_markdown_path("driver_multi"))
    ),
    shiny::fluidRow(
      iatlas.modules::optionsBox(
        width = 12,
        shiny::column(
          width = 3,
          shiny::uiOutput(ns("response_options"))
        ),
        shiny::column(
          width = 3,
          shiny::numericInput(
            ns("min_mutants"),
            "Minimum number of mutant samples per group:",
            2,
            min = 2
          )
        ),
        shiny::column(
          width = 3,
          shiny::numericInput(
            ns("min_wildtype"),
            "Minimum number of wild type samples per group:",
            2,
            min = 2
          )
        ),
        shiny::column(
          width = 3,
          shiny::selectInput(
            ns("group_mode"),
            "Select or Search for Mode",
            choices = c("By group", "Across groups"),
            selected = "Across groups"
          )
        ),
      )
    ),
    model_selection_ui(ns("module1")),
    shiny::fluidRow(
      iatlas.modules::optionsBox(
        width = 12,
        shiny::column(
          width = 6,
          shiny::textOutput(ns("model_text"))
        ),
        shiny::column(
          width = 6,
          shiny::actionButton(ns("calculate_button"), "Calculate")
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
