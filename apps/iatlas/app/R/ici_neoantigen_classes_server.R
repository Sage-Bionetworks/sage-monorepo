ici_neoantigen_classes_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      #get the count data for the samples in the cohort_obj
      count_df <- arrow::read_arrow("inst/feather/neoantigen_classes_count.feather")


      cohort_count <- shiny::reactive({
        cohort_patients <- cohort_obj()$sample_tbl %>%
          dplyr::inner_join(iatlas.api.client::query_sample_patients(), by = "sample_name") %>%
          dplyr::inner_join(count_df, by = "patient_name") %>%
          dplyr::mutate(ERROR = NA) %>%
          dplyr::group_by(dataset_name, group_name, feature_name) %>%
          dplyr::mutate(y = sum(feature_value)) %>%
          dplyr::select(dataset_name, group_name, feature_name, y, ERROR) %>%
          dplyr::distinct()
      })

      all_plots <- shiny::reactive({
        shiny::req(cohort_count())

        purrr::map(cohort_obj()$dataset_names, function(x){
          cohort_count() %>%
            dplyr::filter(dataset_name == x) %>%
            create_barplot(
              .,
              x_col = "feature_name",
              y_col = "y",
              error_col = "ERROR",
              key_col = NA,
              color_col = "group_name",
              label_col = NA,
              xlab = "",
              ylab = "",
              title = unique(.$dataset_name),
              source_name = NULL,
              bar_colors = cohort_obj()$plot_colors,
              showlegend = FALSE
            )
        })
      })

      output$neoantigen_classes_plot <- plotly::renderPlotly({
        shiny::req(all_plots())
        plotly::subplot(all_plots(), nrows = 1)
      })
    }
  )
}
