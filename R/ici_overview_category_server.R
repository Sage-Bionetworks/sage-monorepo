ici_overview_category_server <- function(
  id,
  ioresponse_data
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      ici_datasets <- shiny::reactive(iatlas.api.client::query_datasets(types = "ici"))
      ici_samples <- shiny::reactive(iatlas.api.client::query_dataset_samples(datasets = ici_datasets()$name))

      categories <- shiny::reactive(iatlas.api.client::query_tags(datasets = ici_datasets()$name))

      output$select_group1 <- renderUI(
        shiny::selectInput(ns("group1"), "Select Category",
                    choices = categories()$tag_short_display,
                    selected = "Responder")
      )

      output$select_group2 <- renderUI({
        shiny::req(input$group1)
        shiny::selectInput(ns("group2"), "Select second category to see groups overlap",
                    choices = (categories() %>%
                                 dplyr::filter(tag_short_display != input$group1))$tag_short_display,
                    selected = "Drug")
      })

      group1 <- reactive({
        shiny::req(input$group1)
        convert_value_between_columns(input_value = input$group1,
                                      df = categories(),
                                      from_column = "tag_short_display",
                                      to_column = "tag_name")
      })

      group2 <- reactive({
        shiny::req(input$group2)
        convert_value_between_columns(input_value = input$group2,
                                      df = categories(),
                                      from_column = "tag_short_display",
                                      to_column = "tag_name")
      })


      output$ici_groups_df <- DT::renderDT({
        DT::datatable(categories() %>%
                        dplyr::filter(tag_short_display %in% input$group1) %>%
                        dplyr::select(Category = tag_short_display, Definition = tag_characteristics), #, `Sample Groups`, `Available for`),
                      rownames = FALSE,
                      options = list(dom = 't'))
      })

      values_for_group1 <- shiny::reactive({
        shiny::req(group1())
        iatlas.api.client::query_tag_samples(samples = ici_samples()$sample_name, parent_tags = group1()) %>%
          merge(., ici_samples(), by = "sample_name")
      })

      output$ici_per_ds_df <- DT::renderDT({
        shiny::req(input$group1)
        DT::datatable(get_io_overview_table(values_for_group1()),
                      rownames = FALSE,
                      width = "80%",
                      caption = paste("Group Size per dataset for", input$group1),
                      options = list(dom = 't',
                                     pageLength = 50,
                                     order = list(list(0, 'asc'))))
      })

      output$ici_mosaic <- plotly::renderPlotly({
        shiny::req(values_for_group1(), group2())

        df_mosaic <- get_io_mosaic_df(values_for_group1(), group2())

        df_colors <- df_mosaic %>%
          dplyr::select(y, plot_color) %>%
          dplyr::distinct()

        plot_colors <- df_colors$plot_color
        names(plot_colors) <-  as.character(df_colors$y)

        suppressWarnings( #format_plot throws an warning with the mosaic plot - supressing warning, investigate if possible to change format_plot()
        create_mosaicplot(df_mosaic %>% dplyr::select(x,y),
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
