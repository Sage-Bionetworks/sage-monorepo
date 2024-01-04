sc_immune_features_distribution_server <- function(id, cohort_obj, gsea_df, feature_op, clinical_info){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      #TODO: change this when data is in cohort_obj
      dataset_display <- shiny::reactive(setNames(c("MSK - SCLC", "Vanderbilt - colon polyps"), c("MSK", "Vanderbilt")))
      responder_display <- shiny::reactive(setNames(c("Responder", "Non-Responder", "Unknown"), c("true_responder", "false_responder", "unknown_responder")))

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
          feature_op(), #unique(gsea_df()$feature_name),
          # selected = "APM2"
        )
      })

      output$group2 <- renderUI({
        #Second level group option
        selectInput(
          ns("groupvar2"),
          "Select extra Sample Group (optional)",
          c("None" = "None",
            "Responder" = "Responder"),
          selected = "None"
        )
      })

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


      varible_display_name <- shiny::reactive({
        input$var1_surv
        # convert_value_between_columns(input_value = input$var1_surv,
        #                               df = feature_df,
        #                               from_column = "name",
        #                               to_column = "display")
      })

      varible_plot_label <- shiny::reactive({
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
        samples <- gsea_df() %>%
          dplyr::filter(feature_name == input$var1_surv) %>%
          build_distribution_io_df(., "feature_value", input$scale_method)

        if(input$groupvar2 != "None"){
          # samples %>%
          #   dplyr::rename(group = group_name)
        #}else{
          samples <- samples %>%
            dplyr::left_join(dplyr::select(clinical_info(), HTAN_Assayed_Biospecimen_ID, Responder), by = dplyr::join_by("sample_name" == "HTAN_Assayed_Biospecimen_ID")) %>%
            dplyr::mutate(Responder = dplyr::if_else(
              sample_name == "sum",
              "sum",
              responder_display()[Responder]
            ),
            group_name = paste(group, Responder, sep = " - ")) %>%
            dplyr::select("feature_name", "feature_value", "dataset_name", "sample_name", "group" = group_name, "y")
        }
        samples
      })

      dataset_displays <- shiny::reactive({
        names_datasets <- unique(df_selected()$dataset_name)
        setNames(names_datasets, names_datasets)
      })

      group_colors <- shiny::reactive({
        shiny::req(df_selected())
        group_colors <- grDevices::colorRampPalette(RColorBrewer::brewer.pal(12, "Set3"))(dplyr::n_distinct(df_selected()$group))
        setNames(group_colors, unique(df_selected()$group))
        #setNames(RColorBrewer::brewer.pal(dplyr::n_distinct(df_selected()$group), "Set2"), unique(df_selected()$group))
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
                                    add_title_subplot_plotly(dataset_display()[[x]])%>%
                                    plotly::layout(
                                      xaxis = list(automargin = TRUE,
                                                   tickangle = 80,
                                                   categoryorder = input$reorder_method_choice
                                      ),
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

      drilldown_df <- reactive({
        shiny::req(df_selected())

        eventdata <- plotly::event_data("plotly_click", source = "distPlots")
        shiny::validate(need(!is.null(eventdata), "Click plot above"))

        clicked_group <- gsub("<br />", "\n", eventdata$x[[1]])
        clicked_dataset <- eventdata$customdata[[1]]

        current_groups <- df_selected() %>%
          dplyr::filter(dataset_name == clicked_dataset) %>%
          dplyr::select(group) %>%
          unique

        shiny::validate(
          shiny::need(clicked_group %in% current_groups$group, "Click plot above"))

        df_selected() %>%
          dplyr::filter(dataset_name == clicked_dataset & group == clicked_group)
      })

      output$drilldown_plot <- plotly::renderPlotly({
        shiny::req(drilldown_df())
        create_histogram(
          df = drilldown_df(),
          x_col = "y",
          title = paste(get_plot_title(unique(drilldown_df()$dataset_name), dataset_displays()), unique(drilldown_df()$group), sep = "\n"),
          x_lab = varible_plot_label()
        )
      })

      output$download_hist <- downloadHandler(
        filename = function() stringr::str_c("drilldown-", Sys.Date(), ".csv"),
        content = function(con) readr::write_csv(drilldown_df(), con)
      )
    }
  )
}
