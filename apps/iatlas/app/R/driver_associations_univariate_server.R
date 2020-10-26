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

      response_variable_display <- shiny::reactive({
        shiny::req(input$response_choice)
        cohort_obj() %>%
          purrr::pluck("feature_tbl") %>%
          dplyr::filter(.data$name == input$response_choice) %>%
          dplyr::pull("display")
      })

      tags <- shiny::reactive({
        iatlas.api.client::query_tags(
          datasets = cohort_obj()$dataset,
          parent_tags = cohort_obj()$group_name
        ) %>%
          dplyr::pull("name")
      })

      volcano_plot_tbl <- shiny::reactive({

        shiny::req(
          tags(),
          input$response_choice,
          input$min_wt,
          input$min_mut
        )

        tbl <-
          iatlas.api.client::query_driver_results(
            datasets = cohort_obj()$dataset,
            tags = tags(),
            features = input$response_choice,
            min_num_wild_types = input$min_wt,
            min_num_mutants = input$min_mut
          ) %>%
          dplyr::mutate(label = paste0(
            .data$tag_name, "; ", .data$hgnc, ":", .data$mutation_code
          )) %>%
          dplyr::select(
            "label",
            "p_value",
            "log10_p_value",
            "log10_fold_change",
            "group" = "tag_name",
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

      selected_volcano_result <- shiny::reactive({
        shiny::req(volcano_plot_tbl())

        eventdata <- plotly::event_data(
          "plotly_click",
          source = "univariate_volcano_plot"
        )

        # plot not clicked on yet
        shiny::validate(shiny::need(
          !is.null(eventdata),
          paste0(
            "Click a point on the above scatterplot to see a violin plot ",
            "for the comparison"
          )
        ))

        clicked_label <- get_values_from_eventdata(eventdata, "key")

        result <-  dplyr::filter(
          volcano_plot_tbl(),
          label == clicked_label
        )

        #plot clicked on but event data stale due to parameter change
        shiny::validate(shiny::need(
          nrow(result) == 1,
          paste0(
            "Click a point on the above scatterplot to see a violin plot ",
            "for the comparison"
          )
        ))
        return(result)
      })

      violin_tbl <- shiny::reactive({
        shiny::req(selected_volcano_result(), input$response_choice)
        feature_tbl <-
          iatlas.api.client::query_feature_values(
            datasets = cohort_obj()$dataset,
            features = input$response_choice,
            tags = selected_volcano_result()$group
          )
        status_tbl <-
          iatlas.api.client::query_mutations_by_samples(
            entrez = selected_volcano_result()$entrez,
            mutation_codes = selected_volcano_result()$mutation_code,
            mutation_types = "driver_mutation",
            samples = cohort_obj()$sample_tbl$sample
          )
        dplyr::inner_join(feature_tbl, status_tbl, by = "sample") %>%
          dplyr::select(x = .data$status, y = .data$feature_value)

      })

      output$violin_plot <- plotly::renderPlotly({
        shiny::req(
          selected_volcano_result(),
          response_variable_display(),
          violin_tbl()
        )

        shiny::validate(shiny::need(
          nrow(violin_tbl()) > 0,
          "Parameters have changed, press the calculate boutton."
        ))

        create_violinplot(
          violin_tbl(),
          xlab = stringr::str_c(
            selected_volcano_result()$label, " Mutation Status"
          ),
          ylab = response_variable_display(),
          title = paste(
            "Cohort:",
            selected_volcano_result()$group, ";",
            "P-value:",
            round(selected_volcano_result()$p_value, 4), ";",
            "Log10(Fold Change):",
            round(selected_volcano_result()$log10_fold_change, 4)
          ),
          fill_colors = c("blue"),
          showlegend = FALSE
        )
      })

      plotly_server(
        "violin_plot",
        plot_tbl = violin_tbl
      )

    }
  )
}
