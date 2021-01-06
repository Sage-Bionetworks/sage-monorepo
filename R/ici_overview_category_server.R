ici_overview_category_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      output$select_group2 <- renderUI(
        selectInput(ns("group2"), "Select second category to see groups overlap",
                    choices = (ioresponse_data$categories_df %>%
                                 dplyr::filter(CategoryLabel != input$group))$CategoryLabel)
      )

      group1 <- reactive({
        iatlas.app::convert_value_between_columns(input_value = input$group,
                                      df = ioresponse_data$feature_df,
                                      from_column = "FriendlyLabel",
                                      to_column = "FeatureMatrixLabelTSV")
      })

      group2 <- reactive({
        iatlas.app::convert_value_between_columns(input_value = input$group2,
                                      df = ioresponse_data$feature_df,
                                      from_column = "FriendlyLabel",
                                      to_column = "FeatureMatrixLabelTSV")
      })



      output$ici_groups_df <- DT::renderDT({
        DT::datatable(ioresponse_data$categories_df %>%
                        dplyr::filter(CategoryLabel == input$group) %>%
                        dplyr::select(Category = CategoryLabel, Definition, `Sample Groups`, `Available for`),
                      rownames = FALSE,
                      options = list(dom = 't'))
      })

      output$ici_per_ds_df <- DT::renderDT({
        shiny::req(input$group)

        DT::datatable(get_io_overview_table(group1()) %>%
                        data.table::setcolorder(c("Order", "Sample Group", "Group Name", "Plot Color")),
                      rownames = FALSE,
                      caption = paste("Group Size per dataset for", input$group),
                      options = list(dom = 't',
                                     order = list(list(0, 'asc')))) %>%
          DT::formatStyle(
            'Plot Color',
            backgroundColor = DT::styleEqual(unique("Plot Color"), "Plot Color"))

      })

      output$ici_mosaic <- renderPlotly({
        shiny::req(input$group, input$group2)

        df_mosaic <- iatlas.app::get_io_mosaic_df(ioresponse_data$fmx_df, group1(), group2())

        df_colors <- df_mosaic %>%
          dplyr::select(y, plot_color) %>%
          distinct()

        plot_colors <- c("#C9C9C9", df_colors$plot_color)
        names(plot_colors) <- c("Not annotated", as.character(df_colors$y))

        iatlas.app::create_mosaicplot(df_mosaic %>% dplyr::select(x,y),
                                      title = stringr::str_c(input$group2, "by", input$group, sep = " "),
                                      fill_colors = plot_colors) %>%
                    layout(
                      autosize = TRUE,
                      margin = list(b=0)
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
