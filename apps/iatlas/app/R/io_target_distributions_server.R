io_target_distributions_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

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
        distplot_xlab = shiny::reactive(cohort_obj()$group_name),
        drilldown  = shiny::reactive(T)
      )

      # io_target_tbl <- shiny::reactive({
      #   iatlas.api.client::query_io_targets()
      # })
      #
      # url_gene <- shiny::reactive({
      #   query <- shiny::parseQueryString(session$clientData$url_search)
      #   get_gene_from_url(query)
      # })
      #
      # output$gene_choice_ui <- shiny::renderUI({
      #   shiny::req(input$group_choice, io_target_tbl())
      #   shiny::selectInput(
      #     ns("gene_choice"),
      #     label = "Select or Search Gene",
      #     choices = create_io_target_gene_list(
      #       io_target_tbl(),
      #       input$group_choice
      #     ),
      #     selected = url_gene()
      #   )
      # })
      #
      # gene_choice_hgnc <- shiny::reactive({
      #   shiny::req(input$gene_choice, io_target_tbl())
      #   get_io_hgnc_from_tbl(io_target_tbl(), input$gene_choice)
      # })
      #
      # gene_plot_label <- shiny::reactive({
      #   shiny::req(gene_choice_hgnc(), input$scale_method_choice)
      #
      #   transform_feature_string(
      #     gene_choice_hgnc(),
      #     input$scale_method_choice
      #   )
      # })
      #
      # distplot_tbl <- shiny::reactive({
      #   shiny::req(
      #     cohort_obj(),
      #     input$gene_choice,
      #     input$scale_method_choice
      #   )
      #   build_io_target_distplot_tbl(
      #     cohort_obj(),
      #     as.integer(input$gene_choice),
      #     input$scale_method_choice
      #   )
      # })
      #
      # distribution_plot_server(
      #   "io_targets_dist_plot",
      #   cohort_obj,
      #   distplot_tbl    = distplot_tbl,
      #   distplot_type   = shiny::reactive(input$plot_type_choice),
      #   distplot_ylab   = gene_plot_label,
      #   distplot_title  = gene_choice_hgnc
      # )
    }
  )
}
