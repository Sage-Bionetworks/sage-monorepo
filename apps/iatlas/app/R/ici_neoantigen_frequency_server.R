ici_neoantigen_frequency_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      #get the count data for the samples in the cohort_obj
      top_mhc_df <- arrow::read_feather("inst/feather/neoantigen_top_pmhc.feather")


      plot_df <- shiny::reactive({
        cohort_obj()$sample_tbl %>%
          dplyr::inner_join(iatlas.api.client::query_sample_patients(), by = "sample_name") %>%
          dplyr::inner_join(top_mhc_df, by = "patient_name") %>%
          dplyr::group_by(dataset_name, pmhc, group_name) %>%
          dplyr::summarise(n = dplyr::n(), n_pat= dplyr::n_distinct(patient_name), .groups = "keep")
      })

      all_plots <- shiny::reactive({
        shiny::req(plot_df())

        purrr::map(cohort_obj()$dataset_names, function(x){
          dataset_df <-  plot_df() %>%
            dplyr::filter(dataset_name == x)

          if(nrow(dataset_df)>0){
            dataset_df %>%
              dplyr::mutate_at("group_name", as.factor) %>%
              create_scatterplot(
                .,
                x_col = "n",
                y_col = "n_pat",
                key_col = NA,
                color_col = "group_name",
                label_col = "pmhc",
                xlab = "Frequency of pMHC",
                ylab = "Number of patients with pMHC",
                source_name = "neo_plot",
                fill_colors = unique(cohort_obj()$plot_colors)
              ) %>%
              add_title_subplot_plotly(x)
          }
        }) %>% Filter(Negate(is.null),.)
      })

      output$neoantigen_frequency_plot <- plotly::renderPlotly({
        shiny::req(all_plots())
        plotly::subplot(all_plots(), nrows = 1, shareX = TRUE, shareY = TRUE)
      })
    }
  )
}
