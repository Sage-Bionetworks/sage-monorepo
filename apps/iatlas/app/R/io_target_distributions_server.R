io_target_distributions_server <- function(
  id,
  cohort_obj,
  mock_event_data = shiny::reactive(NULL)
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      url_gene <- shiny::reactive({
        query <- shiny::parseQueryString(session$clientData$url_search)
        get_gene_from_url(query)
      })

      default_gene <- shiny::reactive({
        if(is.na(url_gene())) return(NULL)
        else return(url_gene())
      })

      sample_data_function <- shiny::reactive({
        function(.feature){

          cohort_obj()$get_gene_values(entrez = as.integer(.feature)) %>%
            dplyr::select(
              "sample_name",
              "group_name" = "group_short_name",
              "feature_name" = "entrez",
              "feature_display" = "hgnc",
              "feature_value" = "rna_seq_expr"
            ) %>%
            dplyr::mutate(
              "dataset_name" = cohort_obj()$dataset_names,
              "feature_name" = as.character(.data$feature_name)
            )
        }
      })

      feature_data <- shiny::reactive({
        iatlas.api.client::query_io_targets() %>%
          dplyr::select(
            "feature_name" = "entrez",
            "feature_display" = "hgnc",
            "Pathway" = "pathway",
            "Therapy Type" = "therapy_type"
          ) %>%
          dplyr::mutate("feature_name" = as.character(.data$feature_name))
      })

      group_data <- shiny::reactive({
        cohort_obj()$group_tbl %>%
          dplyr::select(
            "group_name" = "short_name",
            "group_description" = "characteristics",
            "group_color" = "color"
          ) %>%
          dplyr::mutate("group_display" = .data$group_name)
      })

      result <- iatlas.modules::distributions_plot_server(
        "distplot",
        sample_data_function,
        feature_data = feature_data,
        group_data = group_data,
        feature_default = default_gene,
        distplot_xlab = shiny::reactive(cohort_obj()$group_display),
        scale_method_default = shiny::reactive("Log10"),
        drilldown  = shiny::reactive(T),
        mock_event_data = mock_event_data
      )

    }
  )
}
