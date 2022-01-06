ici_neoantigen_correlations_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      #get the count data for the samples in the cohort_obj
      count_df <- arrow::read_feather("inst/feather/neoantigen_classes_count.feather")

      cohort_count <- shiny::reactive({
        cohort_patients <- cohort_obj()$sample_tbl %>%
          dplyr::inner_join(iatlas.api.client::query_sample_patients(), by = "sample_name") %>%
          dplyr::inner_join(count_df, by = "patient_name") %>%
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
          choices  = unique(count_df$feature_name)
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
        shiny::req( input$feature_class_choice)
        cohort_count() %>%
          dplyr::filter(antigen_class == input$neoantigen_feature_choice) %>%
          dplyr::inner_join(.,cohort_obj()$get_feature_values(feature_classes = input$feature_class_choice), by = "sample_name") %>%
          dplyr::select(-c(dataset_name, dataset_display)) %>%
          dplyr::distinct() %>%
          dplyr::inner_join(cohort_obj()$sample_tbl, by = "sample_name") %>%
          dplyr::mutate(x_axis = paste(group_name, dataset_name, sep = "\n")) %>%
          dplyr::group_by(x_axis, feature_name) %>%
          dplyr::summarise(COR = stats::cor(
            feature_value,
            antigen_count,
            method = input$summarise_function_choice,
            use = "pairwise.complete.obs"
          ),
          .groups = "keep") %>%
          tidyr::pivot_wider(names_from = x_axis, values_from = COR) %>%
          tibble::column_to_rownames("feature_name")
      })

      output$plot <- plotly::renderPlotly({
        shiny::req(feature_data())

        create_heatmap(as.matrix(feature_data()), "heatmap")
      })
    }
  )
}
