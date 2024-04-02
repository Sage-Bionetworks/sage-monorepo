sc_umap_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      umap_df <- shiny::reactive(arrow::read_feather("inst/feather/sc_umap.feather") %>% dplyr::filter(dataset %in% input$datasets))

      #TODO: change this when data is in cohort_obj
      dataset_display <- shiny::reactive(setNames(c("MSK - SCLC", "Vanderbilt - colon polyps"), c("MSK", "Vanderbilt")))
      link_to_cellxgene <- shiny::reactive(setNames(c("<a href = 'https://cellxgene.cziscience.com/e/76347874-8801-44bf-9aea-0da21c78c430.cxg/'>Explore in CELLxGENE</a>",
                                                      "<a href = 'https://cellxgene.cziscience.com/e/6a270451-b4d9-43e0-aa89-e33aac1ac74b.cxg/'>Explore in CELLxGENE</a>"),
                                                    c("MSK", "Vanderbilt")))
      color_criteria <- shiny::reactive({
        shiny::req(umap_df(), input$color)
        dplyr::select(umap_df(), "group" = input$color, dataset)
      })


      group_colors <- shiny::reactive({
        shiny::req(umap_df(), color_criteria())
        group_colors <- grDevices::colorRampPalette(RColorBrewer::brewer.pal(12, "Set3"))(dplyr::n_distinct(color_criteria()$group))
        setNames(group_colors, unique(color_criteria()$group))
      })


      output$umap_plot <- plotly::renderPlotly({
        shiny::req(umap_df(),color_criteria(), group_colors())

        datasets <- unique(umap_df()$dataset)

        all_plots <- purrr::map(.x = datasets,
                                .f = function(x){
                                    umap_df() %>%
                                      dplyr::filter(dataset == x) %>%
                                      plotly::plot_ly(
                                        x = ~ umap_1,
                                        y = ~ umap_2,
                                        type = "scatter",
                                        color = ~(dplyr::filter(color_criteria(), dataset == x))$group,
                                        colors = group_colors(),
                                        mode = "markers",
                                        showlegend = FALSE
                                      )%>%
                                    add_title_subplot_plotly(dataset_display()[[x]])%>%
                                    plotly::layout(
                                      xaxis = list(zeroline = F,
                                                   showgrid = F,
                                                   showticklabels = F),
                                      yaxis = list(zeroline = F,
                                                   showgrid = F,
                                                   showticklabels = F),
                                      margin = list(b = 10, t = 70),
                                      plot_bgcolor  = "rgb(250, 250, 250)"
                                    ) %>%
                                    plotly::add_annotations(
                                      x = 1,
                                      xref = "paper",
                                      xanchor = "right",
                                      y = 1,
                                      yref = "paper",
                                      yanchor = "bottom",
                                      text = link_to_cellxgene()[[x]],
                                      showarrow = FALSE
                                    )
                                })
        plotly::subplot(all_plots, nrows = length(datasets), shareX = FALSE, titleX = FALSE, titleY= FALSE, margin = 0.1)

      })


      plot_legend <- shiny::reactive({
        tbl <- dplyr::distinct(
          data.frame(
            b = group_colors(),
            a = names(group_colors())
          ))

        DT::datatable(
          tbl,
          rownames = FALSE,
          class = "",
          callback = DT::JS("$('table.dataTable.no-footer').css('border-bottom', 'none');"),
          options = list(
            dom = 't',
            lengthChange = FALSE,
            headerCallback = DT::JS(
              "function(thead, data, start, end, display){",
              "  $(thead).remove();",
              "}")
          )
        ) %>%
          DT::formatStyle('b', backgroundColor = DT::styleEqual(tbl$b, tbl$b)) %>%
          DT::formatStyle('b', color = DT::styleEqual(tbl$b, tbl$b))

      })

      output$legend <- DT::renderDT(plot_legend())

    }
  )
}
