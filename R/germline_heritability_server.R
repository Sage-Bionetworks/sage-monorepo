germline_heritability_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      heritability <- reactive({
        GERMLINE_PATH = "inst/extdata/"
        feather::read_feather(paste0(GERMLINE_PATH, "tcga_heritability.feather"))
      })

      ns <- session$ns

      plot_title <- reactive({
        if( is.null( input$byImmune) |  input$byImmune == 0) "V(Genotype)/Vp"
        else "V(Genotype x Immune Subbtype)/Vp"
      })

      hdf <- reactive({
        shiny::req(input$ancestry)
        #Reading in the table with computed statistics
        if(input$ancestry == "European" & input$byImmune == TRUE){
          df <- heritability() %>%
            dplyr::filter(cluster == "European_immune")
        }else{
          df <- heritability() %>%
            dplyr::filter(cluster == input$ancestry)
        }

        df %>%
          dplyr::filter(pval <= input$pvalue) %>%
          create_plotly_label(
            ., paste(.$display, "- ", input$ancestry, "Ancestry"),
            paste("\n Immune Trait Category:",.$Annot.Figure.ImmuneCategory, "\n Immune Trait Module:", .$Annot.Figure.ImmuneModule),
            c("Variance", "SE", "pval","FDR"),
            title = "Immune Trait"
          )
      })

      output$heritability <- plotly::renderPlotly({
        shiny::req(hdf())
        shiny::validate(
          shiny::need(nrow(hdf())>0, "No Immune Trait with a p-value lower than selected.")
        )

        if(is.numeric(hdf()[[input$order_bars]]))  plot_levels <-levels(reorder(hdf()[["display"]], hdf()[[input$order_bars]], sort))
        else plot_levels <- (hdf() %>%
              dplyr::arrange(.[[input$order_bars]], Variance))$display %>%
              as.factor()

        hdf() %>%
          dplyr::rename(LRT_p_value = pval) %>% #changing column name to legend title display
            create_barplot_horizontal(
              df = .,
              x_col = "Variance",
              y_col = "display",
              error_col = "SE",
              key_col = NA,
              color_col = "LRT_p_value",
              label_col = "label",
              xlab = "Heritability",
              ylab = "",
              order_by = plot_levels,
              title = plot_title(),
              showLegend = TRUE,
              legendTitle = "LRT \n p-value",
              source_name = "heritability_plot",
              bar_colors = NULL
            ) %>%
          plotly::layout(
            xaxis = list(
              tickformat = "%"
            )
          ) %>%
          plotly::add_annotations(x = hdf()$Variance+hdf()$SE+0.01,
                                  y = hdf()$display,
                                  text = (hdf()$plot_annot),
                                  xref = "x",
                                  yref = "y",
                                  showarrow = F,
                                  font=list(color='black')) %>%
          plotly::add_annotations( text="LRT FDR \n † <= 0.1 \n * <= 0.05 \n ** <= 0.01 \n *** <= 0.001", xref="paper", yref="paper",
                                   x=1.03, xanchor="left",
                                   y=0, yanchor="bottom",
                                   legendtitle=TRUE, showarrow=FALSE )
      })

      output$heritability_cov <- plotly::renderPlotly({

        eventdata <- plotly::event_data( "plotly_click", source = "heritability_plot")
        sub_clusters <- c("Covar:Immune Subtype", "C1", "C2", "C3")

        shiny::validate(
          shiny::need(!is.null(eventdata),
                      "Click bar plot"))
        selected_plot_trait <- eventdata$y[[1]]

        hdf_plot <-heritability()%>%
          dplyr::filter(cluster %in% sub_clusters & display == selected_plot_trait)

        plot_colors <- c("#bebebe", "#FF0000", "#FFFF00", "#00FF00")
        names(plot_colors) <- sub_clusters

        hdf_plot$cluster <- factor(hdf_plot$cluster, levels = c("C3", "C2", "C1", "Covar:Immune Subtype" ))

        create_barplot_horizontal(
          df = hdf_plot,
          x_col = "Variance",
          y_col = "cluster",
          error_col = "SE",
          key_col = NA,
          color_col = "cluster",
          label_col = NA,
          xlab = "",
          ylab = "",
          title = paste("Random data for", selected_plot_trait),
          showLegend = FALSE,
          source_name = NULL,
          bar_colors = plot_colors
        )

      })
    }
  )
}
