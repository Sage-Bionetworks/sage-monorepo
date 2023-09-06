sc_immune_features_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      gsea_df <- shiny::reactive(read.delim("inst/tsv/sc_msk_gsea_norm.csv", sep = ","))

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
          colnames(gsea_df()),
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
          dplyr::select(X, input$var1_surv) %>%
          tidyr::pivot_longer(- "X", names_to = "feature_name", values_to = "feature_value") %>%
          tidyr::drop_na() %>%
          dplyr::mutate(
            "sample_name" = dplyr::if_else(
              stringr::str_starts(.data$X, "HTA8"),
              stringr::str_sub(X, 1, 11),
              "sum"
            ),
            "group" = dplyr::if_else(
              str_starts(.data$X, "HTA8"),
              stringr::str_sub(X, 13),
              X
            )
          )

      })

      group_colors <- shiny::reactive({
        shiny::req(df_selected())

        RColorBrewer::brewer.pal(dplyr::n_distinct(df_selected()$group), "Set2")
      })

      output$dist_plots <- plotly::renderPlotly({
        shiny::req(df_selected())

        plot_function()(df = df_selected(),
                  x_col = "group",
                  y_col = "feature_value",
                  xlab = "group",
                  ylab = input$var1_surv,
                  #custom_data = as.character(dataset),
                  fill_colors = group_colors(),
                  #source = "p1",
                  showlegend = F)  #%>%
          #add_title_subplot_plotly(plot_title) %>%
          # plotly::layout(
          #   # xaxis = xform,
          #   # margin = list(b = 10),
          #   plot_bgcolor  = "rgb(250, 250, 250)"
          # )
      })
      # output$plot_text
      # output$download_tbl



    }
  )
}
