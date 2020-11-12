distribution_plot_server <- function(
  id,
  cohort_obj,
  distplot_tbl,
  distplot_type,
  distplot_ylab,
  distplot_title
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      distplot_function <- shiny::reactive({
        switch(
          distplot_type(),
          "Violin" = create_violinplot,
          "Box" = create_boxplot
        )
      })

      output$distplot <- plotly::renderPlotly({
        shiny::req(distplot_tbl(), distplot_function())

        distplot_function()(
          df = distplot_tbl(),
          source_name = "distplot",
          fill_colors = cohort_obj()$plot_colors,
          ylab = distplot_ylab(),
          xlab = cohort_obj()$group_name,
          title = distplot_title()
        )
      })

      distplot_eventdata <- shiny::reactive({
        shiny::req(distplot_tbl(), distplot_function())
        plotly::event_data("plotly_click", "distplot")
      })

      plotly_server(
        "dist_plot",
        plot_tbl       = distplot_tbl,
        plot_eventdata = distplot_eventdata,
        group_tbl      = shiny::reactive(cohort_obj()$group_tbl)
      )

      # histplot ----------------------------------------------------------------

      histplot_title <- shiny::reactive({
        distplot_eventdata()$x[[1]]
      })

      histplot_tbl <- shiny::reactive({
        eventdata <- distplot_eventdata()
        shiny::validate(shiny::need(!is.null(eventdata), "Click plot above"))
        clicked_group <- eventdata$x[[1]]

        current_groups <- distplot_tbl() %>%
          dplyr::pull(x) %>%
          unique

        shiny::validate(
          shiny::need(clicked_group %in% current_groups, "Click plot above")
        )

        distplot_tbl() %>%
          dplyr::filter(x == clicked_group) %>%
          dplyr::select(-"x") %>%
          dplyr::rename(x = y)
      })

      output$histplot <- plotly::renderPlotly({
        shiny::req(histplot_tbl())
        create_histogram(
          df = histplot_tbl(),
          source_name = "histplot",
          x_lab = distplot_ylab(),
          title = histplot_title()
        )
      })

      plotly_server(
        "hist_plot",
        plot_tbl = histplot_tbl
      )
    }
  )
}
