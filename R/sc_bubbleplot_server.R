sc_bubbleplot_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      bubble_df <- shiny::reactive(read.delim("inst/tsv/bubble_plot_df.tsv"))

      output$select_cells <- shiny::renderUI({
        shiny::req(bubble_df())
        shiny::selectizeInput(
          ns("cells"),
          label = "Select cells",
          choices = unique(bubble_df()$cell_type),
          selected = c("B cell", "Plasma cell",  "CD8+ Teff"),
          multiple = TRUE
        )
      })

      shiny::observe({
        #shiny::req(bubble_df())
        shiny::updateSelectizeInput(
          session,
          "genes",
          choices = unique(bubble_df()$gene),
          selected = c("CTLA4", "PDCD1", "LAG3"),
          server = TRUE
        )
      })

      output$bubble_plot <- plotly::renderPlotly({
        shiny::req(bubble_df(), input$genes, input$cells)

        plot_df <- dplyr::filter(bubble_df(), cell_type %in% input$cells & gene %in% input$genes) %>%
          dplyr::mutate(text = paste(
                        paste0("% cells with expression for gene: ", round(perc_expr, 3)*100, "%"),
                        paste("Average value:", round(avg, 3)), sep = "\n"
                        ))

        create_scatterplot(
          df = plot_df,
          x_col = "gene",
          y_col = "cell_type",
          color_col = "avg",
          size_col = "perc_expr",
          label_col = "text",
          xlab = "Gene Symbol",
          ylab = "Cell type"
        )
      })

    }
  )
}
