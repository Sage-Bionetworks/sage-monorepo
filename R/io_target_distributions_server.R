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

      default_gene <- shiny::reactive({
        if(is.na(url_gene())) return(NULL)
        else return(url_gene())
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
          cohort_obj()$get_gene_values(entrez = as.integer(.feature)) %>%
            dplyr::select(
              "sample" = "sample_name",
              "group" = "group_short_name",
              "feature" = "hgnc",
              "feature_value" = "rna_seq_expr",
              "group_description" = "group_characteristics",
              "color" = "group_color"
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
