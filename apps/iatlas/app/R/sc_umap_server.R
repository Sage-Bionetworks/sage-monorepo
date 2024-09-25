sc_umap_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      all_umap <- shiny::reactive({
        arrow::read_feather("inst/feather/sc_umap_coord.feather")
        })

      dataset_display <- reactive({
        shiny::req(cohort_obj())
        setNames(cohort_obj()$dataset_names, cohort_obj()$dataset_displays)
      })

      output$select_dataset <- shiny::renderUI({
        shiny::selectInput(
          ns("datasets"),
          "Choose dataset(s)",
          choices = names(dataset_display())
        )
      })

      umap_df <-shiny::eventReactive(input$plot_button,{
        all_umap() %>%
          dplyr::filter(dataset_name == dataset_display()[input$datasets])
        })

      link_to_cellxgene <- shiny::reactive(setNames(c("<a href = 'https://cellxgene.cziscience.com/e/76347874-8801-44bf-9aea-0da21c78c430.cxg/'>Explore in CELLxGENE</a>",
                                                      "<a href = 'https://cellxgene.cziscience.com/e/6a270451-b4d9-43e0-aa89-e33aac1ac74b.cxg/'>Explore in CELLxGENE</a>",
                                                      "<a href = 'https://cellxgene.cziscience.com/e/bd65a70f-b274-4133-b9dd-0d1431b6af34.cxg/'>Explore in CELLxGENE</a>",
                                                      "<a href = 'https://microenvironment-of-kidney-cancer.cellgeni.sanger.ac.uk/umap/'>Explore in CELLxGENE</a>",
                                                      "<a href = 'https://singlecell.broadinstitute.org/single_cell/study/SCP1288/tumor-and-immune-reprogramming-during-immunotherapy-in-advanced-renal-cell-carcinoma#study-visualize'>Explore in Single Cell Portal</a>",
                                                      ""),
                                                    c("MSK", "Vanderbilt", "Krishna_2021", "Li_2022", "Bi_2021", "Shiao_2024")))


      group_colors <- shiny::eventReactive(input$plot_button,{
        #shiny::req(umap_df(), color_criteria())
        group_colors <- grDevices::colorRampPalette(RColorBrewer::brewer.pal(12, "Set3"))(dplyr::n_distinct(umap_df()$cell_type))
        setNames(group_colors, unique(umap_df()$cell_type))
      })


      output$umap_plot <- plotly::renderPlotly({
        shiny::req(umap_df(), group_colors())

        datasets <- unique(umap_df()$dataset_name)

        all_plots <- purrr::map(.x = datasets,
                                .f = function(x){
                                    umap_df() %>%
                                      dplyr::filter(dataset_name == x) %>%
                                      plotly::plot_ly(
                                        x = ~ umap_1,
                                        y = ~ umap_2,
                                        type = "scatter",
                                        color = ~(dplyr::filter(umap_df(), dataset_name == x))$cell_type,
                                        colors = group_colors(),
                                        mode = "markers",
                                        height = "600",
                                        showlegend = FALSE
                                      )%>%
                                    add_title_subplot_plotly(names(dataset_display()[dataset_display() == datasets]))%>%
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
