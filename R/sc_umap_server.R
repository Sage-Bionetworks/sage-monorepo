sc_umap_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      umap_df <- shiny::reactive(read.delim("inst/tsv/sc_msk_umap.tsv", sep = "\t"))

      output$select_color_criteria <- shiny::renderUI({
        shiny::req(umap_df())
        shiny::selectInput(
          ns("color"),
          label = "Color by",
          choices = c("cell_type", "cell_type_broad", "tissue", "subtype"),
          selected = "cell_type",
          multiple = FALSE
        )
      })


      output$umap_plot <- plotly::renderPlotly({
        shiny::req(umap_df(), input$color)

        umap_df() %>%
          plotly::plot_ly(
            x = ~ umap_1, y = ~ umap_2,
            type = "scatter", color = ~umap_df()[[input$color]], mode = "markers"
          )
      })

    }
  )
}
