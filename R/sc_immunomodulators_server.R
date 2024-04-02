sc_immunomodulators_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      gsea_df <- shiny::reactive(arrow::read_feather("inst/feather/sc_pseudobulk_gene_expr.feather"))
      sc_clinical <- shiny::reactive(arrow::read_feather("inst/feather/sc_clinical.feather"))


      #TODO: change this when data is in cohort_obj
      dataset_display <- shiny::reactive(setNames(c("MSK - SCLC", "Vanderbilt - colon polyps"), c("MSK", "Vanderbilt")))

      bubble_df <- shiny::reactive({
        #arrow::read_feather("inst/feather/bubble_plot_df.feather")
        iatlasGraphQLClient::query_cell_stats()
      })

      genes <- shiny::reactive({
        iatlasGraphQLClient::query_immunomodulators() %>%
          dplyr::filter(entrez %in% (bubble_df()$gene_entrez))%>%
          dplyr::select(
            name = entrez,
            display = hgnc,
            class = gene_family
          ) #%>%
          # create_nested_list_by_class()
      })

      shiny::observe({
        shiny::req(genes())
        shiny::updateSelectizeInput(
          session,
          "genes",
          choices =  create_nested_list_by_class(genes()),
          selected = c(1493, 3902),
          server = TRUE
        )
      })

      plot_df <- shiny::reactive({
        shiny::req(bubble_df(), input$genes)

        plot_df <- bubble_df() %>%
          dplyr::filter(gene_entrez %in% input$genes) %>%
          dplyr::filter(dataset_name %in% input$datasets) %>%
          dplyr::inner_join(genes(), by = dplyr::join_by(gene_entrez == name)) %>%
          dplyr::mutate(show_text = paste(
            paste0("% cells with expression for gene: ", round(perc_expr, 3)*100, "%"),
            paste("Average value:", round(avg_expr, 3)), sep = "\n"
          ),
          dataset_display = dataset_display()[dataset_name])
      })

      bubble_plot_ggplot <- function(df){

        df %>%
          ggplot2::ggplot(aes(x=type, y=display, text = show_text, size=perc_expr, color=avg_expr, fill = avg_expr)) +
          ggplot2::geom_point(aes(color=avg_expr), pch = 21) +
          ggplot2::scale_color_viridis_c(option = "viridis") +
          ggplot2::scale_fill_viridis_c(option = "viridis") +
          ggplot2::theme_minimal() +
          ggplot2::ylab("Gene Symbol") +
          ggplot2::xlab("Cell type") +
          ggplot2::facet_wrap(~dataset_display, ncol = 1)+
          ggplot2::theme(strip.text = ggplot2::element_text(size = 12),
                         axis.text.x = ggplot2::element_text(size = 10, angle = 315, hjust = 1, vjust = 0.5),
                         title = ggplot2::element_text(size = 12),
                         panel.spacing.y = unit(3, "lines"))
      }


      output$bubble_plot <- plotly::renderPlotly({
        shiny::req(plot_df())

        plotly::ggplotly(bubble_plot_ggplot(plot_df()), tooltip = "show_text", source = "bubbleplot")%>%
          plotly::layout(
            font = list(
              family = "Roboto, Open Sans, sans-serif")
          )
      })

      # shiny::observeEvent(plotly::event_data("plotly_click","bubbleplot"), {
      #
      #   clicked <- plotly::event_data(event = "plotly_click", source = "bubbleplot")
      #   print(clicked)
      # })


      # sc_bubbleplot_server(
      #   "sc_bubbleplot",
      #   cohort_obj
      # )


      sc_immune_features_distribution_server(
        "sc_immunomodulators_distribution",
        cohort_obj,
        gsea_df,
        feature_op = shiny::reactive(input$genes),
        sc_clinical
      )

      observeEvent(input$method_link,{
        shiny::showModal(modalDialog(
          title = "Method",
          includeMarkdown("inst/markdown/methods/sc-pseudobulk.md"),
          easyClose = TRUE,
          footer = NULL
        ))
      })


    }
  )
}
