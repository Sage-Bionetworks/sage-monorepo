ici_distribution_server <- function(
  id,
  ioresponse_data,
  variable_options,
  metadata_feature_df,
  feature_values
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      output$feature_op <- renderUI({
        selectInput(
          ns("var1_surv"),
          "Select or Search for Variable",
          variable_options %>% iatlas.app::create_nested_list_by_class()
        )
      })

      output$group1 <- renderUI({

        clin_data <- metadata_feature_df %>%
          dplyr::filter(`Variable Class` %in% c("Immune Checkpoint Treatment - Response",
                                                "Immune Checkpoint Treatment - Study Condition",
                                                "Sample Category") &
                          `FeatureMatrixLabelTSV` != "Study")

        var_choices <- create_filtered_nested_list_by_class(feature_df = clin_data,
                                                            filter_value = "Categorical",
                                                            class_column = "Variable Class",
                                                            internal_column = "FeatureMatrixLabelTSV",
                                                            display_column = "FriendlyLabel",
                                                            filter_column = "VariableType")
        selectInput(
          ns("groupvar"),
          "Select Sample Group",
          var_choices,
          selected = "Responder"
        )
      })

      output$group2 <- renderUI({ #Second level group option include dataset-specific classes
        lapply(unlist(datasets_options), function(x){

          clin_data <- metadata_feature_df %>%
            dplyr::filter(`Variable Class` == paste("Clinical data for", x) | `Variable Class` %in% c("Immune Checkpoint Treatment - Response", "Immune Checkpoint Treatment - Study Condition", "Sample Category"))

          var_choices <- create_filtered_nested_list_by_class(feature_df = clin_data,
                                                              filter_value = "Categorical",
                                                              class_column = "Variable Class",
                                                              internal_column = "FeatureMatrixLabelTSV",
                                                              display_column = "FriendlyLabel",
                                                              filter_column = "VariableType")
          var_choices[["Default"]] <- c("None" = "None")

          shinyjs::hidden(
            div(id=ns(paste0('div_',x)),
                selectInput(
                  ns(paste0("dist", x)),
                  label = paste("Select extra group for", as.character(x)),
                  choices = var_choices,
                  selected = "None"
                )
            ))})
      })

      # Show all divs that are selected, hide all divs that are not selected.
      shiny::observeEvent(input$datasets, #ignoreNULL = FALSE, ignoreInit = FALSE,
                   {
                     to_hide = setdiff(unlist(datasets_options),input$datasets)
                     for(x in to_hide)
                     {
                       shinyjs::hide(paste0("div_", x))
                     }
                     to_show = input$datasets
                     for(x in to_show)
                     {
                       shinyjs::show(paste0('div_',x))
                     }
                   })

      all_groups <-  shiny::reactive({
        lapply(unlist(datasets_options), function(x) input[[paste0("dist", x)]]) %>%
          unlist() %>%
          unique()
      })

      output$ui_stat <- shiny::renderUI({
        req(input$groupvar)
        if(input$groupvar == "treatment_when_collected" | "treatment_when_collected" %in% all_groups()){
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

        group_df <- suppressWarnings(feature_values %>%
                                       dplyr::select(Sample_ID, Patient_ID, Dataset, treatment_when_collected, input$groupvar, dplyr::one_of(all_groups())))

        group_df <- purrr::map_dfr(unlist(datasets_options), df = group_df, function(x, df){
          g2 <- paste0("dist", x)


          df %>%
            filter(Dataset == x) %>%
            tidyr::drop_na(input$groupvar) %>%
            `if`(
              input$groupvar != "treatment_when_collected" & input[[g2]] != "treatment_when_collected",
              dplyr::filter(., treatment_when_collected == "Pre"),
              .
            ) %>%
            `if`(
              input[[g2]] != "None",
              tidyr::drop_na(., input[[g2]]),
              .
            ) %>%
            combine_groups(., as.character(input$groupvar), as.character(input[[g2]]))
        })

        new_df <- feature_values %>%
          dplyr::select(Sample_ID, input$var1_surv) %>%
          tidyr::drop_na() %>%
          build_distribution_io_df(.,
                                   feature = .[[input$var1_surv]],
                                   scale_func_choice = input$scale_method)

        merge(group_df, new_df, by = "Sample_ID")
      })

      output$dist_plots <- plotly::renderPlotly({
        shiny::validate(
          shiny::need(!is.null(input$datasets), "Select at least one dataset."))
        shiny::req(df_selected())

        all_plots <- purrr::map(.x = input$datasets, function(dataset){

          group1 <- input$groupvar
          group2 <- paste0("dist", dataset)

          dataset_data <- df_selected() %>%
            dplyr::filter(Dataset == dataset)

          if(nrow(dataset_data)>0){
            if(input[[group2]] == "None" | input$groupvar == input[[group2]]){#only one group selected

              dataset_data %>%
                create_plot_onegroup(.,
                                     plot_function(),
                                     dataset,
                                     "y",
                                     "group",
                                     reorder_function = input$reorder_method_choice,
                                     varible_plot_label())

            }else{ #when two grouping levels are selected

              dataset_data %>%
                create_plot_twogroup(.,
                                     plot_function(),
                                     dataset,
                                     "y",
                                     "group",
                                     input$groupvar,
                                     input[[group2]],
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
        shiny::req(input$groupvar)
        shiny::req(df_selected())

        group_label <- convert_value_between_columns(input_value = input$groupvar,
                                                     df = metadata_feature_df,
                                                     from_column = "FeatureMatrixLabelTSV",
                                                     to_column = "FriendlyLabel")
        purrr::map_dfr(.x = input$datasets,
                       df = df_selected(),
                       group_to_split = "group",
                       sel_feature = input$var1_surv,
                       paired = paired_test(),
                       test = test_function(),
                       label = group_label,
                       .f = get_stat_test)
      })

      output$stats1 <- DT::renderDataTable({
        shiny::req(input$groupvar)
        shiny::req(df_selected())
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
          dplyr::filter(Dataset == clicked_dataset) %>%
          dplyr::select(group) %>%
          unique

        shiny::validate(need(gsub("<br />", "\n", data$x[[1]]) %in% current_groups$group, " "))

        key_value <- data %>%
          dplyr::slice(1) %>%
          magrittr::extract2("x") %>%
          strsplit(., " & <br /> ")

        purrr::map_chr(unlist(key_value), function(x)
          paste0(x, ": ",
                 ioresponse_data$sample_group_df[which(ioresponse_data$sample_group_df$FeatureLabel == x), "Characteristics"], "\n"))
      })

      drilldown_df <- reactive({
        shiny::validate(need(!is.null(input$datasets), " "))
        shiny::req(df_selected())

        eventdata <- plotly::event_data("plotly_click", source = "distPlots")
        shiny::validate(need(!is.null(eventdata), "Click plot above"))

        clicked_group <- gsub("<br />", "\n", eventdata$x[[1]]) #eventdata$x[[1]]
        clicked_dataset <- eventdata$customdata[[1]]

        current_groups <- df_selected() %>%
          dplyr::filter(Dataset == clicked_dataset) %>%
          dplyr::select(group) %>%
          unique

        shiny::validate(
          shiny::need(clicked_group %in% current_groups$group, "Click plot above"))

        df_selected() %>%
          dplyr::filter(Dataset == clicked_dataset & group == clicked_group)
      })

      output$drilldown_plot <- plotly::renderPlotly({
        shiny::req(drilldown_df())
          create_histogram(
            df = drilldown_df(),
            x_col = "y",
            title = paste(unique(drilldown_df()$Dataset), unique(drilldown_df()$group), sep = ", "),
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
