sc_immune_features_distribution_server <- function(id, cohort_obj, gsea_df){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      gsea_df <- shiny::reactive(arrow::read_feather("inst/feather/sc_gsea_norm.feather"))

      plot_function <- shiny::reactive({
        switch(
          input$plot_type,
          "Violin" = create_violinplot,
          "Box" = create_boxplot
        )
      })

      output$feature_op <- shiny::renderUI({
        shiny::selectInput(
          ns("var1_surv"),
          "Select Feature",
          unique(gsea_df()$feature_name),
          selected = "APM2"
        )
      })

      output$group2 <- renderUI({
        #Second level group option
        selectInput(
          ns("groupvar2"),
          "Select extra Sample Group (optional)",
          c("None" = "None"),
          selected = "None"
        )
      })

      output$excluded_dataset <- shiny::renderText({
        "" #update once we have the sc data into the cohort object
      })

      df_selected <- shiny::reactive({
        shiny::req(gsea_df(), input$var1_surv)
        gsea_df() %>%
          dplyr::filter(feature_name == input$var1_surv)
      })

      group_colors <- shiny::reactive({
        shiny::req(df_selected())

        setNames(RColorBrewer::brewer.pal(dplyr::n_distinct(df_selected()$group), "Set2"), unique(df_selected()$group))
      })

      output$dist_plots <- plotly::renderPlotly({
        shiny::req(df_selected())

        datasets <- unique(df_selected()$dataset)

        all_plots <- purrr::map(.x = datasets,
                                .f = function(x){
                                  plot_function()(df =  dplyr::filter(df_selected(), dataset == x),
                                                  x_col = "group",
                                                  y_col = "feature_value",
                                                  xlab = "group",
                                                  ylab = input$var1_surv,
                                                  custom_data = as.character(x),
                                                  fill_colors = group_colors(),
                                                  #source = "p1",
                                                  showlegend = F)  %>%
                                    add_title_subplot_plotly(x) %>%
                                    plotly::layout(
                                      # xaxis = xform,
                                      margin = list(b = 10),
                                      plot_bgcolor  = "rgb(250, 250, 250)"
                                    )
                                })

        plotly::subplot(all_plots, nrows = 1, shareY = TRUE, titleX = TRUE, titleY= TRUE, margin = 0.1)


      })




    }
  )
}
