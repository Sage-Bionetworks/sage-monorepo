sc_umap_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::messageBox(
      width = 24,
      shiny::p("UMAP")
    ),
    shiny::fluidRow(
      shiny::column(
        width = 3,
        iatlas.modules::optionsBox(
          width=24,
          shiny::selectInput(
            ns("color"),
            label = "Color by",
            choices = c("cell_type", "cell_type_broad", "tissue", "subtype", "gene"),
            selected = "cell_type_broad",
            multiple = FALSE
          ),
          parameter_tabs <- tabsetPanel(
            id = ns("params"),
            type = "hidden",
            tabPanel("normal",
                     shiny::p("")
            ),
            tabPanel("gene",
                     shiny::uiOutput(ns("select_gene"))
            )
          )
        )
      ),
      shiny::column(
        width = 9,
        iatlas.modules::plotBox(
          width=24,
          plotly::plotlyOutput(ns("umap_plot"))
        )
      )
    )
  )
}
