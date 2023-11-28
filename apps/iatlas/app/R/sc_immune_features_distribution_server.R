sc_immune_features_distribution_server <- function(id, cohort_obj, gsea_df){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      gsea_df <- shiny::reactive(arrow::read_feather("inst/feather/sc_gsea_norm.feather"))

      plot_function <- shiny::reactive({
        switch(
          input$plot_type,
          "Violin" = create_violinplot,
          "Box" = create_boxplot
        )
      })

      output$feature_op <- shiny::renderUI({
        shiny::selectInput(
          ns("var1_surv"),
          "Select Feature",
          unique(gsea_df()$feature_name),
          selected = "APM2"
        )
      })

      # output$group2 <- renderUI({
      #   #Second level group option
      #   selectInput(
      #     ns("groupvar2"),
      #     "Select extra Sample Group (optional)",
      #     c("None" = "None"),
      #     selected = "None"
      #   )
      # })

      output$excluded_dataset <- shiny::renderText({
        "" #update once we have the sc data into the cohort object
      })

      output$ui_stat <- shiny::renderUI({
        #TODO: Update when data is integrated to cohort_obj
        # req(cohort_obj(), input$groupvar2)
        # if(cohort_obj()$group_name == "Sample_Treatment" | input$groupvar2 == "Sample_Treatment"){
        #   radioButtons(ns("paired"), "Sample type", choices = c("Independent", "Paired"), inline = TRUE, selected = "Paired")
        # }else{
          radioButtons(ns("paired"), "Sample type", choices = c("Independent", "Paired"), inline = TRUE, selected = "Independent")
        #}
      })


      group_colors <- shiny::reactive({
        shiny::req(df_selected())

        setNames(RColorBrewer::brewer.pal(dplyr::n_distinct(df_selected()$group), "Set2"), unique(df_selected()$group))
      })

      varible_display_name <- shiny::reactive({
        input$var1_surv
        # convert_value_between_columns(input_value = input$var1_surv,
        #                               df = feature_df,
        #                               from_column = "name",
        #                               to_column = "display")
      })

      varible_plot_label <- reactive({
        switch(
          input$scale_method,
          "None" = varible_display_name(),

          "Log2" = stringr::str_c(
            "Log2( ",
            varible_display_name(),
            " )"),

          "Log2 + 1" = stringr::str_c(
            "Log2( ",
            varible_display_name(),
            " + 1 )"),

          "Log10" = stringr::str_c(
            "Log10( ",
            varible_display_name(),
            " )"),

          "Log10 + 1" = stringr::str_c(
            "Log10( ",
            varible_display_name(),
            " + 1 )")
        )
      })

      df_selected <- shiny::reactive({
        shiny::req(gsea_df(), input$var1_surv)
        gsea_df() %>%
          dplyr::filter(feature_name == input$var1_surv) %>%
          build_distribution_io_df(., "feature_value", input$scale_method)
      })

      dataset_displays <- reactive({
        names_datasets <- unique(df_selected()$dataset_name)
        setNames(names_datasets, names_datasets)
      })

      output$dist_plots <- plotly::renderPlotly({
        shiny::req(df_selected())

        all_plots <- purrr::map(.x = dataset_displays(),
                                .f = function(x){
                                  plot_function()(df =  dplyr::filter(df_selected(), dataset_name == x),
                                                  x_col = "group",
                                                  y_col = "y",
                                                  xlab = "Cell Type",
                                                  ylab = varible_plot_label(),
                                                  custom_data = as.character(x),
                                                  fill_colors = group_colors(),
                                                  #source = "p1",
                                                  showlegend = F)  %>%
                                    add_title_subplot_plotly(x) %>%
                                    plotly::layout(
                                      # xaxis = xform,
                                      margin = list(b = 10),
                                      plot_bgcolor  = "rgb(250, 250, 250)"
                                    )
                                })

        s <- plotly::subplot(all_plots, shareX = TRUE, shareY = TRUE, nrows = 1, margin = c(0.01, 0.01, 0.01, 0.7))

        s$x$source <- "distPlots"
        s
      })

      output$download_tbl <- downloadHandler(
        filename = function() stringr::str_c("distplot-", Sys.Date(), ".csv"),
        content = function(con) readr::write_csv(df_selected(), con)
      )

      paired_test <- shiny::reactive({
        shiny::req(input$paired)
        switch(
          input$paired,
          "Paired" = TRUE,
          "Independent" = FALSE
        )
      })

      test_function <- shiny::reactive({
        switch(
          input$stattest,
          "t-test" = t.test,
          "Wilcox" = wilcox.test
        )
      })

      test_summary_table <- reactive({
        shiny::req(df_selected())
        shiny::validate(
          shiny::need(nrow(df_selected())>0, "Variable not annotated in the selected dataset(s).")
        )

        purrr::map_dfr(.x =  unique(df_selected()$dataset_name),
                       df = df_selected(),
                       group_to_split = "group",
                       sel_feature = "y",
                       paired = paired_test(),
                       test = test_function(),
                       label = group,
                       dataset_title = dataset_displays(),
                       .f = get_stat_test)
      })

      output$stats1 <- DT::renderDataTable({
        shiny::req(test_summary_table())
        test_summary_table()
      })

      output$download_test <- downloadHandler(
        filename = function() stringr::str_c("test_results-", Sys.Date(), ".csv"),
        content = function(con) readr::write_csv(test_summary_table(), con)
      )





    }
  )
}
