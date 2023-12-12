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

      #TODO: change this when data is in cohort_obj
      dataset_display <- shiny::reactive(setNames(c("MSK - SCLC", "Vanderbilt - colon polyps"), c("MSK", "Vanderbilt")))

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

        if (input$color %in% c("cell_type", "type")) return(dplyr::select(umap_df(), "group" = input$color, dataset))
        else{
          shiny::req(input$gene)
          return(gene_df()[[input$gene]])
        }
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
                                      margin = list(b = 10, t = 70),
                                      plot_bgcolor  = "rgb(250, 250, 250)"
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
