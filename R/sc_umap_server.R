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
          choices = c("cell_type", "cell_type_broad", "tissue", "subtype", "gene"),
          selected = "cell_type",
          multiple = FALSE
        )
      })

      color_criteria <- reactive({
        if (input$color %in% c("cell_type", "cell_type_broad", "tissue", "subtype")) return(umap_df()[[input$color]])
        else{
          gene_df <- shiny::reactive(read.delim("inst/tsv/sc_msk_genes.tsv", sep = "\t"))
          output$select_gene <- shiny::renderUI({
            shiny::req(gene_df())
            shiny::selectInput(
              ns("gene"),
              label = "Select gene",
              choices = colnames(gene_df()),
              multiple = FALSE
            )
          })

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
