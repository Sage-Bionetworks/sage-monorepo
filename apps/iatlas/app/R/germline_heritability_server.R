germline_heritability_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      heritability <- reactive({
        iatlas.api.client::query_heritability_results(datasets = "TCGA")
      })

      ancestry_options <- reactive({
        c("Ad Mixed American" = "American", "African" = "African", "Asian" = "Asian", "European"= "European", "European by Immune Subtype" = "European_immune")
      })

      ns <- session$ns

      output$selection_options <- renderUI({
        shiny::req(input$parameter, heritability())

        if(input$parameter == "cluster") opt <- ancestry_options()

        if(input$parameter == "feature_display"){
          opt <- heritability() %>%
            dplyr::select(feature_display,feature_germline_category) %>%
            dplyr::group_by(feature_germline_category) %>%
            tidyr::nest(data = c(feature_display))%>%
            dplyr::mutate(data = purrr::map(data, tibble::deframe)) %>%
            tibble::deframe()
        }
        if(input$parameter == "feature_germline_category") opt <- unique(heritability()$feature_germline_category)
        if(input$parameter == "feature_germline_module") opt <- unique(heritability()$feature_germline_module)

        shiny::selectizeInput(ns("group"), "Show associated results for", choices = opt, selected = opt[4])

      })

      plot_title <- reactive({
        if(input$parameter == "cluster"){
          if(input$group != "European_immune") "V(Genotype)/Vp"
          else "V(Genotype x Immune Subtype)/Vp"
        } else{
          paste("V(Genotype)/Vp", input$group, sep = " - ")
        }
      })

      hdf <- reactive({
        shiny::req(input$group)
        create_heritability_df(
          heritablity_data = heritability(),
          parameter = input$parameter,
          group = input$group,
          pval_thres =input$pvalue,
          ancestry_labels = ancestry_options()
        )
      })

      output$heritability <- plotly::renderPlotly({
        shiny::req(hdf())
        shiny::validate(
          shiny::need(nrow(hdf())>0, "No Immune Trait with a p-value lower than selected.")
        )

        #order bars
        if(is.numeric(hdf()[[input$order_bars]]))  plot_levels <-levels(reorder(hdf()[["ylabel"]], hdf()[[input$order_bars]], sort))
        else plot_levels <- (hdf() %>%
                               dplyr::arrange(.[[input$order_bars]], variance))$ylabel %>%
          as.factor()

        hdf() %>%
          dplyr::mutate('Neg_log10_p_value' = -log10(p_value)) %>% #changing column name to legend title display
          create_barplot_horizontal(
            df = .,
            x_col = "variance",
            y_col = "ylabel",
            error_col = "se",
            key_col = NA,
            color_col = "Neg_log10_p_value",
            label_col = "text",
            xlab = "Heritability",
            ylab = "",
            order_by = plot_levels,
            title = plot_title(),
            showLegend = TRUE,
            legendTitle = "LRT \n p-value",
            source_name = "heritability_plot",
            bar_colors = NULL
          ) %>%
          format_heritability_plot(., hdf(), fdr = TRUE)
      })

      # output$heritability_cov <- plotly::renderPlotly({
      #
      #   eventdata <- plotly::event_data( "plotly_click", source = "heritability_plot")
      #   sub_clusters <- c("Covar:Immune Subtype", "C1", "C2", "C3")
      #
      #   shiny::validate(
      #     shiny::need(!is.null(eventdata),
      #                 "Click bar plot"))
      #   selected_plot_trait <- eventdata$y[[1]]
      #
      #   hdf_plot <- germline_data$heritability %>%
      #     dplyr::filter(cluster %in% sub_clusters & display == selected_plot_trait)
      #
      #   plot_colors <- c("#bebebe", "#FF0000", "#FFFF00", "#00FF00")
      #   names(plot_colors) <- sub_clusters
      #
      #   hdf_plot$cluster <- factor(hdf_plot$cluster, levels = c("C3", "C2", "C1", "Covar:Immune Subtype" ))
      #
      #   create_barplot_horizontal(
      #     df = hdf_plot,
      #     x_col = "Variance",
      #     y_col = "cluster",
      #     error_col = "SE",
      #     key_col = NA,
      #     color_col = "cluster",
      #     label_col = NA,
      #     xlab = "Heritability",
      #     ylab = "",
      #     title = paste("Random data for", selected_plot_trait),
      #     showLegend = FALSE,
      #     source_name = NULL,
      #     bar_colors = plot_colors
      #   ) %>%
      #     format_heritability_plot(., hdf_plot, fdr = FALSE)
      # })

      observeEvent(input$method_link,{
        shiny::showModal(modalDialog(
          title = "Method",
          includeMarkdown("inst/markdown/methods/germline_heritability.md"),
          easyClose = TRUE,
          footer = NULL
        ))
      })
    }
  )
}
