sc_umap_ui <- function(id){
#TODO: add links to HTAN; add gene info for Vanderbilt
#TODO: incorporate clinical annotation (especially response to treatment)

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::messageBox(
      width = 24,
      shiny::p("UMAP")
    ),
    iatlas.modules::optionsBox(
      width=24,
      shiny::column(
        width = 6,
        shiny::selectInput(
          ns("color"),
          label = "Color by",
          choices = c("Cell Type"="cell_type", "Type" = "type"),
          selected = "cell_type",
          multiple = FALSE
        )
        ),
      # shiny::column(
      #   width = 6,
      #   parameter_tabs <- tabsetPanel(
      #     id = ns("params"),
      #     type = "hidden",
      #     tabPanel("normal",
      #              shiny::p("")
      #     ),
      #     tabPanel("gene",
      #              shiny::uiOutput(ns("select_gene"))
      #     )
      #   )
      # )
    ),
    iatlas.modules::plotBox(
      width=24,
      shiny::column(
        width = 10,
        plotly::plotlyOutput(ns("umap_plot"),
                             height = 800) %>%
          shinycssloaders::withSpinner(.)
      ),
      shiny::column(
        width = 2,
        div(
          DT::DTOutput(ns("legend")),
          style = "font-size: 70%"
        )
      )
    )
  )
}
