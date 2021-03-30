germline_rarevariants_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      rv_data <- reactive({
        iatlas.api.client::query_rare_variant_pathway_associations(datasets = "TCGA")
      })


      trait_choices <- reactive({
        iatlas.modules::create_nested_named_list(
                          rv_data(),
                          names_col1 = "feature_germline_category",
                          names_col2 = "feature_display",
                          values_col = "feature_name"
                        )
      })

      shiny::updateSelectizeInput(session, 'feature',
                                  choices = trait_choices(),
                                  selected = "Bindea_CD8_T_cells",
                                  server = TRUE)

      selected_data <- reactive({
        rv_data() %>%
          dplyr::filter(feature_name == input$feature) %>%
          dplyr::mutate(pathway = stringr::str_replace_all(pathway, "_", " "))
      })

      output$dist_plot <- plotly::renderPlotly({
        shiny::req(input$feature)

        df <- selected_data() %>% tidyr::drop_na()
        plot_levels <- (df %>% dplyr::arrange(desc(.[[input$order_box]])))$pathway

        create_boxplot_from_summary_stats(
           df,
          "pathway",
          "q1",
          "q2",
          "q3",
          "min",
          "max",
          "mean",
          order_by = plot_levels#,
          #color_col = "p_value"#,
         # fill_colors = bar_colors
        )
      })

      output$stats_tbl <- DT::renderDataTable({
        shiny::req(input$feature)
        DT::datatable(
          selected_data() %>% dplyr::select(Pathway = pathway,
                                            "Patients with mutation" = n_mutants,
                                            "Total patients" = n_total,
                                            "p-value" = p_value) ,
          rownames = FALSE,
          options = list(order = list(3, 'asc'))
        ) %>% DT::formatRound(columns= "p-value", digits=3)
      })

      observeEvent(input$method_link,{
        shiny::showModal(modalDialog(
          title = "Method",
          includeMarkdown("inst/markdown/methods/germline-rarevariants.md"),
          easyClose = TRUE,
          footer = NULL
        ))
      })
    }
  )
}
