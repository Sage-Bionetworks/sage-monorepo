ici_overview_category_server <- function(
  id,
  ioresponse_data
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      output$select_group1 <- renderUI(
        selectInput(ns("group1"), "Select Category",
                    choices = ioresponse_data$categories_df$CategoryLabel)
      )

      output$select_group2 <- renderUI({
        shiny::req(input$group1)
        selectInput(ns("group2"), "Select second category to see groups overlap",
                    choices = (ioresponse_data$categories_df %>%
                                 dplyr::filter(CategoryLabel != input$group1))$CategoryLabel)
      })

      group1 <- reactive({
        convert_value_between_columns(input_value = input$group1,
                                      df = ioresponse_data$feature_df,
                                      from_column = "FriendlyLabel",
                                      to_column = "FeatureMatrixLabelTSV")
      })

      group2 <- reactive({
        convert_value_between_columns(input_value = input$group2,
                                      df = ioresponse_data$feature_df,
                                      from_column = "FriendlyLabel",
                                      to_column = "FeatureMatrixLabelTSV")
      })


      output$ici_groups_df <- DT::renderDT({
        DT::datatable(ioresponse_data$categories_df %>%
                        dplyr::filter(CategoryLabel %in% input$group1) %>%
                        dplyr::select(Category = CategoryLabel, Definition, `Sample Groups`, `Available for`),
                      rownames = FALSE,
                      options = list(dom = 't'))
      })

      output$ici_per_ds_df <- DT::renderDT({
        shiny::req(input$group1)

        DT::datatable(iatlas.app::get_io_overview_table(group1(), ioresponse_data) %>%
                        data.table::setcolorder(c("Order", "Sample Group", "Group Name", "Plot Color")),
                      rownames = FALSE,
                      caption = paste("Group Size per dataset for", input$group1),
                      options = list(dom = 't',
                                     order = list(list(0, 'asc')))) %>%
          DT::formatStyle(
            columns = 'Plot Color',
            backgroundColor = DT::styleEqual(unique("Plot Color"), values = "Plot Color"))

      })

      output$ici_mosaic <- plotly::renderPlotly({
        shiny::req(input$group1, input$group2)

        df_mosaic <- iatlas.app::get_io_mosaic_df(ioresponse_data, group1(), group2())

        df_colors <- df_mosaic %>%
          dplyr::select(y, plot_color) %>%
          dplyr::distinct()

        plot_colors <- c("#C9C9C9", df_colors$plot_color)
        names(plot_colors) <- c("Not annotated", as.character(df_colors$y))

        suppressWarnings( #format_plot throws an warning with the mosaic plot - supressing warning, investigate if possible to change format_plot()
        iatlas.app::create_mosaicplot(df_mosaic %>% dplyr::select(x,y),
                                      title = stringr::str_c(input$group2, "by", input$group1, sep = " "),
                                      fill_colors = plot_colors) %>%
                    plotly::layout(
                      autosize = TRUE,
                      margin = list(b=0)
                    )
        )
      })

      observeEvent(input$method_link,{
        shiny::showModal(modalDialog(
                  title = "Method",
                  includeMarkdown("inst/markdown/methods/ResponseICIPredictors.txt"),
                  easyClose = TRUE,
                  footer = NULL
                ))
      })
    }
  )
}
