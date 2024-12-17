ici_neoantigen_correlations_server <- function(
  id,
  cohort_obj,
  count_df,
  dataset_displays
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      cohort_count <- shiny::reactive({
        cohort_patients <- cohort_obj()$sample_tbl %>%
          dplyr::inner_join(count_df,  by = c("sample_name" = "sample")) %>%
          dplyr::select(sample_name, antigen_class = feature_name, antigen_class_display = feature_display, antigen_count = feature_value)
      })

      output$class_selection_ui <- shiny::renderUI({
        classes <- cohort_obj()$feature_tbl$class %>%
          unique() %>%
          sort()

        shiny::selectInput(
          inputId  = ns("feature_class_choice"),
          label    = "Select or Search for Feature Class",
          choices  = classes,
          selected = classes[1]
        )
      })

      output$neoantigen_selection_ui <- shiny::renderUI({
        shiny::selectInput(
          inputId  = ns("neoantigen_feature_choice"),
          label    = "Select or Search for Antigen Class",
          choices  = unique(count_df$feature_name),
          selected = "Self-Antigen"
        )
      })

      output$summarise_function_ui <- shiny::renderUI({
        funs = list(
          "pearson" = purrr::partial(stats::cor, method = "pearson"),
          "Spearman" = purrr::partial(stats::cor, method = "spearman"),
          "Kendall" = purrr::partial(stats::cor, method = "kendall")
        )

        shiny::selectInput(
          inputId  = ns("summarise_function_choice"),
          label    = "Select Summarise Function",
          choices  = names(funs)
        )
      })

      feature_data <- shiny::reactive({
        shiny::req(input$feature_class_choice)

        cohort_count() %>%
          dplyr::filter(antigen_class == input$neoantigen_feature_choice) %>%
          dplyr::inner_join(.,cohort_obj()$get_feature_values(feature_classes = input$feature_class_choice), by = "sample_name") %>%
          dplyr::select(-c(dataset_name, dataset_display)) %>%
          dplyr::distinct() %>%
          dplyr::inner_join(cohort_obj()$sample_tbl, by = "sample_name") %>%
          dplyr::mutate(x_axis = paste(group_name, unname(dataset_displays()[dataset_name]), sep = " - "))
      })

      correlation_data <- shiny::reactive({
        shiny::req(feature_data())
        shiny::validate(shiny::need(nrow(feature_data()) != 0, "There is no neoantigen in the selected class."))

        feature_data() %>%
          dplyr::group_by(x_axis, feature_display) %>%
          dplyr::summarise(COR = stats::cor(
            feature_value,
            antigen_count,
            method = input$summarise_function_choice,
            use = "pairwise.complete.obs"
          ),
          .groups = "keep") %>%
          tidyr::pivot_wider(names_from = x_axis, values_from = COR) %>%
          tibble::column_to_rownames("feature_display")

      })

      output$cor_plot <- plotly::renderPlotly({
        shiny::req(correlation_data())

        create_heatmap(as.matrix(correlation_data()), "heatmap") %>%
          plotly::layout(margin = list(
                           t = 30,
                           b = 10,
                           pad = 1
                         ),
                         xaxis = list(tickangle = 45))

      })

      values <- shiny::reactiveValues(selected_group = NULL)

      shiny::observeEvent(plotly::event_data(event = "plotly_click",
                                             source = "heatmap"), {

        clicked <- plotly::event_data(event = "plotly_click",
                              source = "heatmap")

        if (!is.null(clicked)) {
          values$selected_group <- clicked$x
          values$selected_feature <- clicked$y
        }

      })

      output$scatter_plot <- plotly::renderPlotly({
        shiny::req(feature_data())
        shiny::validate(shiny::need(values$selected_group, message = "Click on heatmap for scatterplot"))

        x <- feature_data() %>%
          dplyr::filter(feature_display == values$selected_feature) %>%
          dplyr::filter(x_axis == values$selected_group) %>%
          dplyr::mutate(text = paste("Count", input$neoantigen_feature_choice, ": ", antigen_count, "\n",
                                     values$selected_feature, ": ", feature_value))

        lm_zero=lm(antigen_count~feature_value, data = x)

          create_scatterplot(
            x,
            x_col = "feature_value",
            y_col = "antigen_count",
            xlab = values$selected_feature,
            ylab = paste("Count", input$neoantigen_feature_choice),
            title = paste(input$neoantigen_feature_choice, "x", values$selected_feature, "\n for", values$selected_group),
            color_col = "group_color",
            label_col = "text",
            show_legend = FALSE
          ) %>%
          plotly::add_lines(x =~feature_value, y = fitted(lm_zero),line = list(color = 'black',dash="dot", alpha = 0.5))
      })

      observeEvent(input$method_link,{
        shiny::showModal(modalDialog(
          title = "Method",
          includeMarkdown("inst/markdown/methods/neoantigen-correlation.markdown"),
          easyClose = TRUE,
          footer = NULL
        ))
      })
    }
  )
}
