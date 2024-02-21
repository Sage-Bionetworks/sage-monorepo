sc_umap_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::titleBox(
      "iAtlas Explorer — UMAP Viewer of Single cell RNA-Seq data"
    ),
    iatlas.modules::textBox(
      width = 12,
      shiny::p("Explore UMAP visualizations of single-cell RNA-Seq datasets.")
    ),
    iatlas.modules::messageBox(
      width = 24,
      shiny::p("Explore UMAP visualizations of single-cell RNA-Seq datasets. Links for visualizations on CELLxGENE are provided for further exploration.")
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
