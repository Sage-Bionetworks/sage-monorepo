sc_umap_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      umap_df <- shiny::reactive(arrow::read_feather("inst/feather/sc_umap.feather"))
      gene_df <- shiny::reactive(arrow::read_feather("inst/feather/sc_msk_genes.feather"))

      observeEvent(input$color, {
        if(input$color == "gene") updateTabsetPanel(inputId = "params", selected = "gene")
        if(input$color %in% c("cell_type", "type")) updateTabsetPanel(inputId = "params", selected = "normal")
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

        if (input$color %in% c("cell_type", "type")) return(dplyr::select(umap_df(), input$color, dataset))
        else{
          shiny::req(input$gene)
          return(gene_df()[[input$gene]])
        }
      })


      output$umap_plot <- plotly::renderPlotly({
        shiny::req(umap_df(), color_criteria())

        datasets <- unique(umap_df()$dataset)

        all_plots <- purrr::map(.x = datasets,
                                .f = function(x){
                                    umap_df() %>%
                                      dplyr::filter(dataset == x) %>%
                                      plotly::plot_ly(
                                        x = ~ umap_1, y = ~ umap_2,
                                        type = "scatter", color = ~(dplyr::filter(color_criteria(), dataset == x))[[input$color]], mode = "markers"
                                      )%>%
                                    add_title_subplot_plotly(x)%>%
                                    plotly::layout(
                                      margin = list(b = 10, t = 70),
                                      plot_bgcolor  = "rgb(250, 250, 250)"
                                    )
                                })
        plotly::subplot(all_plots, nrows = length(datasets), shareX = FALSE, titleX = FALSE, titleY= FALSE, margin = 0.1)

      })

    }
  )
}
