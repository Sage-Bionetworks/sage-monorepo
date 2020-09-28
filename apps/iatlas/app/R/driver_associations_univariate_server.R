univariate_driver_server <- function(id, cohort_obj) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      response_option_list <- shiny::reactive({
        create_nested_named_list(
          cohort_obj()$feature_tbl, values_col = "name"
        )
      })

      output$response_option_ui <- shiny::renderUI({
        shiny::selectInput(
          inputId  = ns("response_choice"),
          label    = "Select or Search for Response Variable",
          selected = "leukocyte_fraction",
          choices  = response_option_list()
        )
      })

      tags <- shiny::reactive({
        iatlas.api.client::query_tags(
          cohort_obj()$dataset,
          cohort_obj()$group_name
        ) %>%
          dplyr::pull("name")
      })

      # TODO: use mutation_id: https://gitlab.com/cri-iatlas/iatlas-api/-/issues/36
      volcano_plot_tbl <- shiny::reactive({
        print("test")
        print(tags())
        print(input$response_variable)
        print(input$min_wt)
        print(input$min_mut)

        shiny::req(
          tags(),
          input$response_variable,
          input$min_wt,
          input$min_mut
        )
        iatlas.api.client::query_driver_results(
          datasets = cohort_obj()$dataset,
          tags = tags(),
          features = input$response_variable,
          min_num_wild_types = input$min_wt,
          min_num_mutants = input$min_mut
        ) %>%
          dplyr::mutate(label = paste0(
            .data$tag_display, "; ", .data$hgnc, ":", .data$mutation_code
          )) %>%
          dplyr::select(
            "log10_fold_change",
            "log10_p_value",
            "group" = "tag_display",
            "label",
            "entrez",
            "mutation_code"
          )
      })

      output$volcano_plot <- plotly::renderPlotly({
        shiny::req(volcano_plot_tbl())

        shiny::validate(shiny::need(
          nrow(volcano_plot_tbl()) > 0,
          paste0(
            "Current parameters did not result in any linear regression",
            "results."
          )
        ))

        create_scatterplot(
          volcano_plot_tbl(),
          x_col       = "log10_fold_change",
          y_col       = "log10_p_value",
          xlab        = "Log10(Fold Change)",
          ylab        = "- Log10(P-value)",
          title       = "Immune Response Association With Driver Mutations",
          source_name = "univariate_volcano_plot",
          key_col     = "label",
          label_col   = "label",
          horizontal_line   = T,
          horizontal_line_y = (-log10(0.05))
        )
      })

      plotly_server(
        "volcano_plot",
        plot_tbl = volcano_plot_tbl
      )

      # selected_volcano_result <- shiny::reactive({
      #   shiny::req(volcano_plot_tbl())
      #
      #   eventdata <- plotly::event_data(
      #     "plotly_click",
      #     source = "univariate_volcano_plot"
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
      #   #plot clicked on but event data stale due to parameter change
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
      # features_tbl <- query_feature_values_with_cohort_object(
      #   cohort_obj(), feature = input$response_variable
      # )
      #
      # status_tbl <-
      #
      # violin_tbl <- shiny::reactive({
      #   shiny::req(selected_volcano_result())
      #   build_ud_driver_violin_tbl(
      #     input$response_variable,
      #     selected_volcano_result()$entrez,
      #     selected_volcano_result()$tag_id,
      #     selected_volcano_result()$mutation_code_id
      #   )
      # })
      #
      # output$violin_plot <- plotly::renderPlotly({
      #
      #   xlab <- paste0(
      #     "Mutation Status ",
      #     selected_volcano_result()$gene,
      #     ":",
      #     selected_volcano_result()$mutation_code
      #   )
      #
      #   ylab <- input$response_variable %>%
      #     as.integer() %>%
      #     get_feature_display_from_id()
      #
      #   title <- paste(
      #     "Cohort:",
      #     selected_volcano_result()$group, ";",
      #     "P-value:",
      #     round(selected_volcano_result()$p_value, 4), ";",
      #     "Log10(Fold Change):",
      #     round(selected_volcano_result()$log10_fold_change, 4)
      #   )
      #
      #   create_violinplot(
      #     violin_tbl(),
      #     xlab = xlab,
      #     ylab = ylab,
      #     title = title,
      #     fill_colors = c("blue"),
      #     showlegend = FALSE
      #   )
      # })
      #
      # plotly_server(
      #   "violin_plot",
      #   plot_tbl = violin_tbl
      # )
      #
      # volcano_plot_server(
      #   "univariate_driver_server",
      #   volcano_plot_tbl,
      #   "Immune Response Association With Driver Mutations",
      #   "univariate_driver_server",
      #   "Wt",
      #   "Mut",
      #   shiny::reactive(input$response_variable)
      # )
    }
  )
}
