ici_distribution_server <- function(
  id,
  cohort_obj,
  metadata_feature_df,
  feature_df
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      output$excluded_dataset <- shiny::renderText({
        if(identical(unique(cohort_obj()$group_tbl$dataset_display), cohort_obj()$dataset_displays)){
          ""
        }else{
          excluded_datasets <- setdiff(cohort_obj()$dataset_displays, unique(cohort_obj()$group_tbl$dataset_display))
          paste(
            paste(excluded_datasets, collapse = ", "),
            " not included because all samples were filtered in ICI Cohort Selection."
          )
        }
      })

      output$feature_op <- renderUI({
        selectInput(
          ns("var1_surv"),
          "Select Feature",
          feature_df %>% create_nested_list_by_class()
        )
      })

      output$group2 <- renderUI({
        #Second level group option
        selectInput(
          ns("groupvar2"),
          "Select extra Sample Group (optional)",
          c("None" = "None", metadata_feature_df),
          selected = "None"
        )
      })

      output$ui_stat <- shiny::renderUI({
        req(cohort_obj(), input$groupvar2)
        if(cohort_obj()$group_name == "Sample_Treatment" | input$groupvar2 == "Sample_Treatment"){
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
        convert_value_between_columns(input_value = input$var1_surv,
                                      df = feature_df,
                                      from_column = "name",
                                      to_column = "display")
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

      dataset_displays <- reactive({
        setNames(cohort_obj()$dataset_displays, cohort_obj()$dataset_names)
      })

      df_selected <- reactive({
        shiny::req(cohort_obj(), input$var1_surv)

        if(input$var1_surv %in% cohort_obj()$feature_tbl$name){
          samples <- cohort_obj()$sample_tbl %>%
            dplyr::inner_join(., iatlas.api.client::query_feature_values(features = input$var1_surv), by = c("sample_name" = "sample")) %>%
            build_distribution_io_df(., "feature_value", input$scale_method)
        }else{
          samples <- cohort_obj()$sample_tbl %>%
            dplyr::inner_join(., iatlas.api.client::query_gene_expression(cohorts = cohort_obj()$dataset_names, entrez = as.numeric(input$var1_surv)), by = c("sample_name" = "sample")) %>%
            build_distribution_io_df(., "rna_seq_expr", input$scale_method)
        }

        if(input$groupvar2 == "None" | cohort_obj()$group_name == input$groupvar2){
          samples %>%
            dplyr::rename(group = group_name)
        }else{
         groups <- samples %>%
           dplyr::inner_join(iatlas.api.client::query_tag_samples(parent_tags = input$groupvar2), by = "sample_name")

         combine_groups(groups, input$groupvar2,cohort_obj()) %>%
           dplyr::inner_join(samples %>% dplyr::select(sample_name, y), by = "sample_name")
        }
      })

      output$dist_plots <- plotly::renderPlotly({
        shiny::req(df_selected())

        all_plots <- purrr::map(.x = cohort_obj()$dataset_names, function(dataset){

          if(input$groupvar2 == "None" | cohort_obj()$group_name == input$groupvar2){#only one group selected

            dataset_data <- df_selected() %>%
              dplyr::filter(dataset_name == dataset)

            if(nrow(dataset_data)>0){
              dataset_data %>%
                create_plot_onegroup(.,
                                     cohort_obj(),
                                     dataset_displays(),
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
                                       cohort_obj = cohort_obj(),
                                       dataset_displays(),
                                       plot_function(),
                                       dataset,
                                       "y",
                                       "group",
                                       cohort_obj()$group_name,
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
        shiny::req(df_selected())
        shiny::validate(
          shiny::need(nrow(df_selected())>0, "Variable not annotated in the selected dataset(s). Select other datasets or check ICI Datasets Overview for more information.")
        )

        purrr::map_dfr(.x =  cohort_obj()$dataset_names,
                       df = df_selected(),
                       group_to_split = "group",
                       sel_feature = "y",
                       paired = paired_test(),
                       test = test_function(),
                       label = cohort_obj()$group_name,
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

      output$plot_text <- shiny::renderText({
        shiny::req(df_selected())

        eventdata <- plotly::event_data("plotly_click", source = "distPlots")
        shiny::validate(need(!is.null(eventdata), " "))

        clicked_dataset <- eventdata$customdata[[1]]

        current_groups <- df_selected() %>%
          dplyr::filter(dataset_name == clicked_dataset)

        shiny::validate(need(gsub("<br />", "\n", eventdata$x[[1]]) %in% unique(current_groups$group), " ")) #remove text in case grouping selection is changed

        key_value <- eventdata %>%
          dplyr::slice(1) %>%
          magrittr::extract2("x") %>%
          gsub("<br />", "\n", .)


        if(input$groupvar2 == "None"){
          if("Immune feature bin range" %in% cohort_obj()$group_tbl$characteristics){
            paste(unique(cohort_obj()$group_tbl$short_name), key_value, sep = ": ")
          }else{
            selected_display <- cohort_obj()$group_tbl %>%
              dplyr::filter(short_name == key_value) %>%
              dplyr::select(short_name, long_name, characteristics) %>%
              dplyr::distinct()
            paste(selected_display$long_name, selected_display$characteristics, sep = ": ")
          }
        }else{
          selected_display <- df_selected() %>%
            dplyr::filter(group == key_value) %>%
            dplyr::select(long_name.x, characteristics.x, long_name.y, characteristics.y) %>%
            dplyr::distinct()
          paste(
            paste(selected_display$long_name.x, selected_display$characteristics.x, sep = ": "),
            paste(selected_display$long_name.y, selected_display$characteristics.y, sep = ": "),
            sep = "\n"
          )
        }
      })

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
          title = paste(unique(unname(dataset_displays()[drilldown_df()$dataset_name])), unique(drilldown_df()$group), sep = "\n"),
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
