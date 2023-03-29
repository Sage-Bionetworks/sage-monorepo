sc_umap_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      umap_df <- shiny::reactive(arrow::read_feather("inst/feather/sc_msk_umap.feather"))
      gene_df <- shiny::reactive(arrow::read_feather("inst/feather/sc_msk_genes.feather"))

      observeEvent(input$color, {
        if(input$color == "gene") updateTabsetPanel(inputId = "params", selected = "gene")
        if(input$color %in% c("cell_type", "cell_type_broad", "tissue", "subtype")) updateTabsetPanel(inputId = "params", selected = "normal")
      })

      output$select_gene <- shiny::renderUI({
        shiny::selectInput(
          ns("gene"),
          label = "Select gene",
          choices = colnames(gene_df()),
          multiple = FALSE
        )
      })

      color_criteria <- shiny::reactive({
        shiny::req(umap_df(), input$color)
        if (input$color %in% c("cell_type", "cell_type_broad", "tissue", "subtype")) return(umap_df()[[input$color]])
        else{
          shiny::req(input$gene)
          return(gene_df()[[input$gene]])
        }
      })


      output$umap_plot <- plotly::renderPlotly({
        shiny::req(umap_df(), color_criteria())

        umap_df() %>%
          plotly::plot_ly(
            x = ~ umap_1, y = ~ umap_2,
            type = "scatter", color = ~color_criteria(), mode = "markers"
          )
      })

    }
  )
}
