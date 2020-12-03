io_target_distributions_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      url_gene <- shiny::reactive({
        query <- shiny::parseQueryString(session$clientData$url_search)
        get_gene_from_url(query)
      })

      features <- shiny::reactive({
        iatlas.api.client::query_io_targets() %>%
          dplyr::select(
            "feature_name" = "entrez",
            "feature_display" = "hgnc",
            "Pathway" = "pathway",
            "Therapy Type" = "therapy_type"
          )
      })

      plot_data_function <- shiny::reactive({
        function(.feature){
          group_data <- cohort_obj()$group_tbl %>%
            dplyr::select("group", "group_description" = "characteristics", "color")
          cohort_obj() %>%
            query_gene_expression_with_cohort_object(entrez = as.integer(.feature)) %>%
            dplyr::inner_join(cohort_obj()$sample_tbl, by = "sample") %>%
            dplyr::inner_join(group_data, by = "group") %>%
            dplyr::select(
              "sample",
              "group",
              "feature" = "hgnc",
              "feature_value" = "rna_seq_expr",
              "group_description",
              "color"
            )
        }
      })

      iatlas.modules::distributions_plot_server(
        "distplot",
        plot_data_function,
        features   = features,
        distplot_xlab = shiny::reactive(cohort_obj()$group_display),
        scale_method_default = shiny::reactive("Log10"),
        feature_default = shiny::reactive(url_gene()),
        drilldown  = shiny::reactive(T)
      )

    }
  )
}
