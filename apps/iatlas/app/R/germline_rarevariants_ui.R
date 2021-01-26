germline_rarevariants_ui <- function(id){

  ns <- shiny::NS(id)
  shiny::tagList(
    messageBox(
      width = 12,
      shiny::p("See rare variants boxplots for different pathways.")
    ),
    optionsBox(
      width = 12,
      shiny::column(
        width = 8,
        shiny::uiOutput(ns("features"))
      ),
      shiny::column(
        width = 4,
        shiny::selectizeInput(ns("order_box"),
                              "Order plots by ",
                              choices = list(
                                             "p-value" = "p_value",
                                             "Median" = "q2",
                                             "Mean" = "mean",
                                             "Min" = "min",
                                             "Max" = "max",
                                             "Number of patients with mutation" = "n_mutation"
                              ),
                              selected = "p_value")
      )
    ),
    plotBox(
      width = 12,
      plotly::plotlyOutput(ns("dist_plot"), height = "700px") %>%
        shinycssloaders::withSpinner(.)
    ),
    messageBox(
      width = 3,
      shiny::p("Tests comparing a group with all other groups were performed.")
    ),
    plotBox(
      width = 9,
      DT::dataTableOutput(ns("stats_tbl"))
    )

  )
}
