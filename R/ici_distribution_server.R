ici_distribution_server <- function(
  id,
  ici_datasets,
  variable_options,
  metadata_feature_df,
  feature_values
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      output$list_datasets <- shiny::renderUI({
        checkboxGroupInput(ns("datasets"), "Select Datasets", choices = ici_datasets, selected = NULL)
      })

      output$feature_op <- renderUI({
        selectInput(
          ns("var1_surv"),
          "Select or Search for Variable",
          variable_options %>% iatlas.app::create_nested_list_by_class()
        )
      })

      output$group1 <- renderUI({
        selectInput(
          ns("groupvar1"),
          "Select Sample Group",
          metadata_feature_df,
          selected = "Responder"
        )
      })

      output$group2 <- renderUI({
        #Second level group option include dataset-specific classes
        selectInput(
          ns("groupvar2"),
          "Select Sample Group",
          c("None" = "None", metadata_feature_df),
          selected = "None"
        )

      })

      output$ui_stat <- shiny::renderUI({
        req(input$groupvar1, input$groupvar2)
        if(input$groupvar1 == "Sample_Treatment" | input$groupvar2 == "Sample_Treatment"){
          radioButtons(ns("paired"), "Sample type", choices = c("Independent", "Paired"), inline = TRUE, selected = "Paired")
        }else{
          radioButtons(ns("paired"), "Sample type", choices = ("Independent"), inline = TRUE, selected = "Independent")
        }
      })

      plot_function <- shiny::reactive({
        switch(
          input$plot_type,
          "Violin" = create_violinplot,
          "Box" = create_boxplot
        )
      })

      varible_display_name <- shiny::reactive({

        convert_value_between_columns(input_value =input$var1_surv,
                                      df = variable_options,
                                      from_column = "INTERNAL",
                                      to_column = "DISPLAY")
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

      df_selected <- reactive({
        samples <- feature_values %>%
          dplyr::filter(feature_name == input$var1_surv) %>%
          dplyr::inner_join(iatlas.api.client::query_dataset_samples(datasets = input$datasets), by = c("sample" = "sample_name")) %>%
          build_distribution_io_df(., "feature_value", input$scale_method)

        if(input$groupvar2 == "None" | input$groupvar1 == input$groupvar2){
          samples %>%
            dplyr::inner_join(iatlas.api.client::query_tag_samples(parent_tags = c(input$groupvar1)), by = c("sample" = "sample_name")) %>%
            dplyr::mutate(group = tag_short_display)

        }else{
           groups <- samples %>%
             dplyr::inner_join(iatlas.api.client::query_tag_samples(parent_tags = c(input$groupvar1, input$groupvar2)), by = c("sample" = "sample_name"))

         combine_groups(groups, input$groupvar1, input$groupvar2) %>%
           dplyr::inner_join(iatlas.api.client::query_dataset_samples(datasets = input$datasets), by = c("sample" = "sample_name")) %>%
           dplyr::inner_join(samples %>% dplyr::select(sample, y), by = "sample") %>%
           dplyr::mutate(group = paste(tag_short_display.x, tag_short_display.y, sep = " & \n"))
        }
      })

      output$dist_plots <- plotly::renderPlotly({
        shiny::validate(
          shiny::need(!is.null(input$datasets), "Select at least one dataset."))
        shiny::req(df_selected())

        all_plots <- purrr::map(.x = input$datasets, function(dataset){

          if(input$groupvar2 == "None" | input$groupvar1 == input$groupvar2){#only one group selected

            dataset_data <- df_selected() %>%
              dplyr::filter(dataset_name == dataset)

            if(nrow(dataset_data)>0){
              dataset_data %>%
                create_plot_onegroup(.,
                                     plot_function(),
                                     dataset,
                                     "y",
                                     "group",
                                     reorder_function = input$reorder_method_choice,
                                     varible_plot_label())
            }

            }else{ #when two grouping levels are selected

              dataset_data <- df_selected() %>%
                dplyr::filter(dataset_name == dataset)

              if(nrow(dataset_data)>0){
                dataset_data %>%
                  create_plot_twogroup(.,
                                       plot_function(),
                                       dataset,
                                       "y",
                                       "group",
                                       input$groupvar1,
                                       input$groupvar2,
                                       reorder_function = input$reorder_method_choice,
                                       varible_plot_label())
            }
          }
        }) %>% Filter(Negate(is.null),.) #excluding datasets that do not have annotaion for the selected variable
        shiny::validate(
          shiny::need(length(all_plots)>0, "Variable not annotated in the selected dataset(s). Select other datasets or check ICI Datasets Overview for more information.")
        )
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
        shiny::req(input$groupvar1, input$datasets, df_selected())
        shiny::validate(
          shiny::need(nrow(df_selected())>0, "Variable not annotated in the selected dataset(s). Select other datasets or check ICI Datasets Overview for more information.")
        )

        purrr::map_dfr(.x = input$datasets,
                       df = df_selected(),
                       group_to_split = "group",
                       sel_feature = "y",
                       paired = paired_test(),
                       test = test_function(),
                       label = input$groupvar1,
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

      output$plot_text <- shiny::renderText({

        shiny::validate(need(!is.null(input$datasets), " "))
        shiny::req(df_selected())

        data <- plotly::event_data("plotly_click", source = "distPlots")

        if (is.null(data)) return(" ")

        clicked_dataset <- data$customdata[[1]]

        current_groups <- df_selected() %>%
          dplyr::filter(dataset_name == clicked_dataset)

        shiny::validate(need(gsub("<br />", "\n", data$x[[1]]) %in% unique(current_groups$group), " ")) #remove text in case grouping selection is changed

        key_value <- data %>%
          dplyr::slice(1) %>%
          magrittr::extract2("x") %>%
          gsub("<br />", "\n", .)

        selected_display <- current_groups %>%
          dplyr::filter(group == key_value) %>%
          dplyr::select(group, tag_name, dplyr::starts_with(c("tag_long_display", "tag_characteristics"))) %>%
          dplyr::distinct()

        if(ncol(selected_display)>4){
          paste(
            paste(selected_display$tag_long_display.x, selected_display$tag_characteristics.x, sep = ": "),
            paste(selected_display$tag_long_display.y, selected_display$tag_characteristics.y, sep = ": "),
            sep = "\n"
          )
        }else{
          paste(selected_display$tag_long_display, selected_display$tag_characteristics, sep = ": ")
        }
      })

      drilldown_df <- reactive({
        shiny::validate(need(!is.null(input$datasets), " "))
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
            title = paste(unique(sub("\\ -.*", "", drilldown_df()$dataset_display)), unique(drilldown_df()$group), sep = ", "),
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
