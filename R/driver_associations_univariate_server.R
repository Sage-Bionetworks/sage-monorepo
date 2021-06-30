univariate_driver_server <- function(id, cohort_obj) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      response_option_list <- shiny::reactive({
        iatlas.modules::create_nested_named_list(
          cohort_obj()$feature_tbl,
          names_col1 = "class",
          names_col2 = "display",
          values_col = "name"
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

      total_associations <- shiny::reactive({
        n_mutations <-
          iatlas.api.client::query_cohort_mutations(cohorts = cohort_obj()$dataset) %>%
          print() %>%
          nrow()

        n_tags <- length(tags())

        n_possible <-  n_tags * n_mutations
      })

      p_tested <- shiny::reactive({
        p_tested <-
          volcano_plot_tbl() %>%
          nrow() %>%
          magrittr::divide_by(., total_associations()) %>%
          round(2)
      })

      output$result_text <- shiny::renderText({
        stringr::str_c(
          "Percentage of Tested Associations: ",
          as.character(p_tested())
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

        clicked_label <- iatlas.modules::get_values_from_eventdata(eventdata, "key")

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
        feature_tbl <- iatlas.modules2::query_feature_values_with_cohort_object(
          cohort_object = cohort_obj(),
          features = input$response_choice,
          groups = selected_volcano_result()$group
        )
        status_tbl <-
          iatlas.api.client::query_mutation_statuses(
            entrez = selected_volcano_result()$entrez,
            codes = selected_volcano_result()$mutation_code,
            types = "driver_mutation",
            samples = cohort_obj()$sample_tbl$sample
          ) %>%
          dplyr::select(
            "sample" = "sample_name",
            "status" = "mutation_status"
          )
        dplyr::inner_join(feature_tbl, status_tbl, by = "sample") %>%
          dplyr::mutate(
            "status" = forcats::fct_relevel(.data$status, "Wt", "Mut")
          ) %>%
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
