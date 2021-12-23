ici_neoantigen_classes_server <- function(
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
          dplyr::mutate(ERROR = NA) %>%
          dplyr::group_by(dataset_name, group_name, feature_name) %>%
          dplyr::mutate(y = sum(feature_value)) %>%
          dplyr::mutate(text = paste("Neoantigen class: ", feature_name, "\n Group: ", group_name, "\n Count: ", y)) %>%
          dplyr::select(dataset_name, group_name, feature_name, y, ERROR, text) %>%
          dplyr::distinct()
      })

      all_plots <- shiny::reactive({
        shiny::req(cohort_count())

        purrr::map(cohort_obj()$dataset_names, function(x){
          dataset_df <-  cohort_count() %>%
            dplyr::filter(dataset_name == x)

          if(nrow(dataset_df)>0){
            dataset_df %>%
              create_barplot(
                .,
                x_col = "feature_name",
                y_col = "y",
                error_col = "ERROR",
                key_col = NA,
                color_col = "group_name",
                label_col = "text",
                title = "",
                source_name = "neo_plot",
                bar_colors = cohort_obj()$plot_colors,
                showlegend = FALSE
              ) %>%
              add_title_subplot_plotly(x)
          }
        }) %>% Filter(Negate(is.null),.)
      })

      output$neoantigen_classes_plot <- plotly::renderPlotly({
        shiny::req(all_plots())
        plotly::subplot(all_plots(), nrows = 1)
      })
    }
  )
}
