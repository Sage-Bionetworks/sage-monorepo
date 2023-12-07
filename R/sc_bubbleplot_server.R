sc_bubbleplot_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      bubble_df <- shiny::reactive(arrow::read_feather("inst/feather/bubble_plot_df.feather"))

      # output$select_cells <- shiny::renderUI({
      #   shiny::req(bubble_df())
      #   shiny::selectizeInput(
      #     ns("cells"),
      #     label = "Select cells",
      #     choices = unique(bubble_df()$cell_type),
      #     selected = c("B cell"),
      #     multiple = TRUE
      #   )
      # })

      shiny::observe({
        # shiny::req(bubble_df())
        shiny::updateSelectizeInput(
          session,
          "genes",
          choices = unique(bubble_df()$gene),
          selected = c("CTLA4", "PDCD1", "LAG3"),
          server = TRUE
        )
      })

      plot_df <- shiny::reactive({
        shiny::req(bubble_df(), input$genes)
        plot_df <- dplyr::filter(bubble_df(), gene %in% input$genes) %>%
          dplyr::mutate(show_text = paste(
                        paste0("% cells with expression for gene: ", round(perc_expr, 3)*100, "%"),
                        paste("Average value:", round(avg, 3)), sep = "\n"
                        ))
      })

      bubble_plot_ggplot <- function(df){

        df %>%
          ggplot2::ggplot(aes(x=cell_type, y=gene, text = show_text, size=perc_expr, color=avg, fill = avg)) +
          ggplot2::geom_point(aes(color=avg), pch = 21) +
          ggplot2::scale_color_viridis_c(option = "viridis") +
          ggplot2::scale_fill_viridis_c(option = "viridis") +
          ggplot2::theme_minimal() +
          ggplot2::ylab("Gene Symbol") +
          ggplot2::xlab("Cell type") +
          ggplot2::theme(strip.text = ggplot2::element_text(size = 12),
                         axis.text.x = ggplot2::element_text(size = 10, angle = 315, hjust = 1, vjust = 0.5),
                         title = ggplot2::element_text(size = 12),
                         panel.spacing.y = unit(3, "lines"))+
          ggplot2::facet_wrap(~dataset, ncol = 1)
      }


      output$bubble_plot <- plotly::renderPlotly({
        shiny::req(plot_df())
        datasets <- unique(plot_df()$dataset)

        plotly::ggplotly(bubble_plot_ggplot(plot_df()), tooltip = "show_text")%>%
          plotly::layout(
            font = list(
              family = "Roboto, Open Sans, sans-serif")
          )

        # all_plots <- purrr::map(.x = datasets,
        #                         .f = function(x){
        #                             create_scatterplot(
        #                               df = dplyr::filter(plot_df(), dataset == x),
        #                               x_col = "cell_type",
        #                               y_col = "gene",
        #                               color_col = "avg",
        #                               size_col = "perc_expr",
        #                               label_col = "text",
        #                               xlab = "Gene Symbol",
        #                               ylab = "Cell type"
        #                             ) %>%
        #                             add_title_subplot_plotly(x)
        #                          })
        #
        # plotly::subplot(all_plots, nrows = 1, shareY = TRUE, titleX = TRUE, titleY= TRUE, margin = 0.1)
      })

    }
  )
}
