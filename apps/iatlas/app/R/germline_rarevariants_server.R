germline_rarevariants_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      rv_data <- reactive({
        iatlas.api.client::query_rare_variant_pathway_associations(datasets = "TCGA")
      })

      output$features <- renderUI({

        # trait_choices <- rv_data() %>%
        #                   dplyr::select(feature_display,feature_germline_category) %>%
        #                   dplyr::group_by(feature_germline_category) %>%
        #                   tidyr::nest(data = c(feature_display))%>%
        #                   dplyr::mutate(data = purrr::map(data, tibble::deframe)) %>%
        #                   tibble::deframe()

        trait_choices <- unique(rv_data()$feature_display)

        shiny::selectInput(ns("feature"),
                           "Search and select Immune Trait",
                           choices = trait_choices,
                           selected = trait_choices[1])
      })

      selected_data <- reactive({
        rv_data() %>%
          dplyr::filter(feature_display == input$feature)
      })

      output$dist_plot <- plotly::renderPlotly({
        shiny::req(input$feature)

        df <- selected_data() %>% tidyr::drop_na()

        #order plots
        if(is.numeric(df[[input$order_box]]))  plot_levels <-levels(reorder(df[["pathway"]], df[[input$order_box]], sort))
        else plot_levels <- (df %>% dplyr::arrange(.[[input$order_box]], p_value))$pathway %>% as.factor()

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
