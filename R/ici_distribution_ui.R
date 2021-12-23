ici_distribution_ui <- function(id){
  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::optionsBox(
      width=12,
      column(
        width = 3,
        shiny::uiOutput(ns("feature_op"))
      ),
      column(
        width = 3,
        shiny:: uiOutput(ns("group2"))
      ),
      column(
        width = 2,
        shiny::selectInput(
                ns("plot_type"),
                "Select Plot Type",
                choices = c("Violin", "Box")
              )
      ),
      column(
        width = 2,
          shiny::selectInput(
                  ns("scale_method"),
                  "Select scaling",
                  choices = c(
                    "None",
                    "Log2",
                    "Log2 + 1",
                    "Log10",
                    "Log10 + 1"
                  ),
                  selected = "None"
          )
      ),
      column(
        width = 2,
        shiny::selectInput(
          ns("reorder_method_choice"),
          "Reorder Function",
          choices = c("None" = "None", "Median", "Mean", "Max", "Min"),
          selected = "None"
        )
      )
    ),#optionsBox
    shiny::htmlOutput(ns("excluded_dataset")),
    iatlas.modules::plotBox(
      width = 12,
      plotly::plotlyOutput(ns("dist_plots"), height = "500px") %>%
        shinycssloaders::withSpinner(),
      tagAppendAttributes(shiny::textOutput(ns("plot_text")), style="white-space:pre-wrap;"),
      shiny::h5("Click plot to see group information."),
      shiny::downloadButton(ns("download_tbl"), "Download plot table")
    ),
    shiny::fluidRow(
      iatlas.modules::optionsBox(
        width = 3,
        shiny::uiOutput(ns("ui_stat")),
        shiny::radioButtons(ns("stattest"), "Test type", choices = c("t-test", "Wilcox"), inline = TRUE, selected = "t-test")
      ),
      iatlas.modules::plotBox(
        width = 9,
        DT::dataTableOutput(ns("stats1")),
        downloadButton(ns('download_test'), 'Download')
      )
    ),
    iatlas.modules::plotBox(
        width = 12,
        plotly::plotlyOutput(ns("drilldown_plot")) %>%
          shinycssloaders::withSpinner(),
        shiny::downloadButton(ns("download_hist"), "Download plot table")
      )
    #)
  )
}
