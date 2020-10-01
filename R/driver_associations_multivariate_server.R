multivariate_driver_server <- function(id, cohort_obj) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      output$response_options <- shiny::renderUI({
        shiny::selectInput(
          inputId  = ns("response_choice"),
          label    = "Select or Search for Response Variable",
          selected = "leukocyte_fraction",
          choices  = create_nested_named_list(
            cohort_obj()$feature_tbl, values_col = "name"
          )
        )
      })

      numerical_covariate_tbl <- shiny::reactive({
        cohort_obj() %>%
          purrr::pluck("feature_tbl") %>%
          dplyr::select("class", "display", "feature" = "name")
      })

      categorical_covariate_tbl <- shiny::reactive({
        dplyr::tribble(
          ~class,     ~display,         ~feature,
          "Groups",   "Immune Subtype", "Immune_Subtype",
          "Groups",   "TCGA Study",     "TCGA_Study",
          "Groups",   "TCGA Subtype",   "TCGA_Subtype"
        )
      })

      response_variable_display <- shiny::reactive({
        shiny::req(input$response_choice)
        cohort_obj() %>%
          purrr::pluck("feature_tbl") %>%
          dplyr::filter(.data$name == input$response_choice) %>%
          dplyr::pull("display")
      })

      model_string_prefix <- shiny::reactive({
        shiny::req(response_variable_display())
        stringr::str_c(response_variable_display(), " ~ Mutation status")
      })

      covariates_obj <- model_selection_server(
        "module1",
        numerical_covariate_tbl,
        categorical_covariate_tbl,
        model_string_prefix
      )

      output$model_text <- shiny::renderText({
        covariates_obj()$display_string
      })

      covariate_tbl <- shiny::reactive({
        shiny::req(covariates_obj())
        build_md_covariate_tbl(cohort_obj(), covariates_obj())
      })

      response_tbl <- shiny::reactive({
        shiny::req(input$response_choice)
        build_md_response_tbl(cohort_obj(), input$response_choice)
      })

      status_tbl <- shiny::reactive({
        iatlas.api.client::query_mutations_by_samples(
          samples = cohort_obj()$sample_tbl$sample
        ) %>%
          dplyr::mutate(
            "mutation" = stringr::str_c(.data$hgnc, ":", .data$mutation_code)
          )
      })

      combined_tbl <- shiny::reactive({
        print(response_tbl)
        print(status_tbl())
        print(covariate_tbl())
        print(input$group_mode)
        shiny::req(
          response_tbl(),
          status_tbl(),
          input$group_mode
        )
        combine_md_tbls(
          response_tbl(),
          cohort_obj()$sample_tbl,
          status_tbl(),
          covariate_tbl(),
          input$group_mode
        )
      })
      #
      # labels <- shiny::reactive({
      #   shiny::req(combined_tbl(), input$min_mutants, input$min_wildtype)
      #   filter_md_labels(combined_tbl(), input$min_mutants, input$min_wildtype)
      # })
      #
      # filtered_tbl <- shiny::reactive({
      #   shiny::req(combined_tbl(), labels())
      #   dplyr::filter(combined_tbl(), label %in% labels())
      # })
      #
      # pvalue_tbl <- shiny::reactive({
      #   shiny::req(filtered_tbl(), covariates_obj())
      #   build_md_pvalue_tbl(filtered_tbl(), covariates_obj()$formula_string)
      # })
      #
      # effect_size_tbl <- shiny::reactive({
      #   shiny::req(filtered_tbl())
      #   build_md_effect_size_tbl(filtered_tbl())
      # })
      #
      # volcano_plot_tbl <- shiny::eventReactive(input$calculate_button, {
      #   shiny::req(pvalue_tbl(), effect_size_tbl())
      #   dplyr::inner_join(pvalue_tbl(), effect_size_tbl(), by = "label")
      # })
      #
      output$volcano_plot <- plotly::renderPlotly({
        # print(covariates_obj)
        # shiny::req(volcano_plot_tbl())
        #
        # shiny::validate(shiny::need(
        #   nrow(volcano_plot_tbl()) > 0,
        #   paste0(
        #     "Current parameters did not result in any linear regression",
        #     "results."
        #   )
        # ))
      #
      # #   create_scatterplot(
      # #     volcano_plot_tbl(),
      # #     x_col     = "log10_fold_change",
      # #     y_col     = "log10_p_value",
      # #     xlab      = "Log10(Fold Change)",
      # #     ylab      = "- Log10(P-value)",
      # #     title     = "Immune Response Association With Driver Mutations",
      # #     source    = "multivariate_volcano_plot",
      # #     key_col   = "label",
      # #     label_col = "label",
      # #     horizontal_line   = T,
      # #     horizontal_line_y = (-log10(0.05))
      # #   )
      })
      #
      # plotly_server(
      #   "volcano_plot",
      #   plot_tbl = volcano_plot_tbl
      # )
      #
      # selected_volcano_result <- shiny::reactive({
      #   shiny::req(volcano_plot_tbl())
      #
      #   eventdata <- plotly::event_data(
      #     "plotly_click",
      #     source = "multivariate_volcano_plot"
      #   )
      #
      #   # plot not clicked on yet
      #   shiny::validate(shiny::need(
      #     !is.null(eventdata),
      #     paste0(
      #       "Click a point on the above scatterplot to see a violin plot ",
      #       "for the comparison"
      #     )
      #   ))
      #
      #   clicked_label <- get_values_from_eventdata(eventdata, "key")
      #
      #   result <-  dplyr::filter(
      #     volcano_plot_tbl(),
      #     label == clicked_label
      #   )
      #
      #   shiny::validate(shiny::need(
      #     nrow(result) == 1,
      #     paste0(
      #       "Click a point on the above scatterplot to see a violin plot ",
      #       "for the comparison"
      #     )
      #   ))
      #   return(result)
      # })
      #
      # violin_tbl <- shiny::reactive({
      #   shiny::req(filtered_tbl(), selected_volcano_result(), input$group_mode)
      #   build_md_driver_violin_tbl(
      #     filtered_tbl(),
      #     selected_volcano_result()$label
      #   )
      # })
      #
      # output$violin_plot <- plotly::renderPlotly({
      #   shiny::req(
      #     selected_volcano_result(),
      #     response_variable_name(),
      #     input$group_mode,
      #     violin_tbl()
      #   )
      #
      #   shiny::validate(shiny::need(
      #     nrow(violin_tbl()) > 0,
      #     "Parameters have changed, press the calculate boutton."
      #   ))
      #
      #   create_violinplot(
      #     violin_tbl(),
      #     xlab = create_md_violin_plot_x_lab(
      #       selected_volcano_result()$label, input$group_mode
      #     ),
      #     ylab = response_variable_name(),
      #     title = create_md_violin_plot_title(
      #       selected_volcano_result(), input$group_mode
      #     ),
      #     fill_colors = c("blue"),
      #     showlegend = FALSE
      #   )
      # })
#
#       plotly_server(
#         "violin_plot",
#         plot_tbl = violin_tbl
#       )
    }
  )
}

