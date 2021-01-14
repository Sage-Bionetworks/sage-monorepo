ici_distribution_ui <- function(id){
  ns <- shiny::NS(id)

  shiny::tagList(
    optionsBox(
      width=12,
      shinyjs::useShinyjs(),
      column(
        width = 3,
        checkboxGroupInput(ns("datasets"), "Select Datasets", choices = datasets_options, selected = NULL)
      ),
      column(
        width = 3,
        shiny::uiOutput(ns("feature_op"))
      ),
      column(
        width = 3,
        shiny::uiOutput(ns("group1")),
        p("Select extra Sample Groups (optional)"),
        shiny:: uiOutput(ns("group2"))
      ),
      column(
        width = 3,
        shiny::selectInput(
                ns("plot_type"),
                "Select Plot Type",
                choices = c("Violin", "Box")
              ),

        shiny::selectInput(
                ns("scale_method"),
                "Select variable scaling",
                choices = c(
                  "None",
                  "Log2",
                  "Log2 + 1",
                  "Log10",
                  "Log10 + 1"
                ),
                selected = "None"
        ),
        shiny::checkboxInput(
                ns("see_drilldown"),
                "Display histogram of distribution by clicking on a violin",
                FALSE
        )
      )
    ),#optionsBox
    plotBox(
      width = 12,
      plotly::plotlyOutput(ns("dist_plots"), height = "500px") %>%
        shinycssloaders::withSpinner(),
      tagAppendAttributes(shiny::textOutput(ns("plot_text")), style="white-space:pre-wrap;"),
      shiny::h5("Click plot to see group information.")
    ),
    shiny::fluidRow(
      optionsBox(
        width = 3,
        shiny::uiOutput(ns("ui_stat")),
        shiny::radioButtons(ns("stattest"), "Test type", choices = c("t-test", "Wilcox"), inline = TRUE, selected = "t-test")
      ),
      plotBox(
        width = 9,
        DT::dataTableOutput(ns("stats1")),
        downloadButton(ns('download_test'), 'Download')
      )
    ),
    shiny::conditionalPanel(
      condition =  "input.see_drilldown",
      ns = ns,
      plotBox(
        width = 12,
        plotly::plotlyOutput(ns("drilldown_plot")) %>%
          shinycssloaders::withSpinner()
      )
    )
  )
}
