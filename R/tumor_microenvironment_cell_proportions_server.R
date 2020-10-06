tumor_microenvironment_cell_proportions_server  <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      plot_data <- shiny::reactive({
        cohort_obj() %>%
          build_ocp_value_tbl() %>%
          dplyr::select(
            "sample",
            "x" = "group",
            "y" = "feature_value",
            "color" = "feature_display"
          )
      })

      iatlas.modules::barplot_server(
        "tumor_microenvironment_cell_proportions",
        plot_data,
        group_data = shiny::reactive(cohort_obj()$group_tbl),
        barplot_xlab  = shiny::reactive("Fraction type by group"),
        barplot_ylab  = shiny::reactive("Fraction mean"),
        barplot_label = shiny::reactive("Sample"),
        drilldown     = shiny::reactive(T)
      )

      # barplot_tbl <- shiny::reactive({
      #   req(value_tbl())
      #   build_ocp_barplot_tbl(value_tbl())
      # })
      #
      # output$barplot <- plotly::renderPlotly({
      #   shiny::req(barplot_tbl())
      #
      #   create_barplot(
      #     barplot_tbl(),
      #     source_name = "overall_cell_proportions_barplot",
      #     color_col = "color",
      #     label_col = "label",
      #     xlab = "Fraction type by group",
      #     ylab = "Fraction mean"
      #   )
      # })
      #
      # barplot_eventdata <- shiny::reactive({
      #   plotly::event_data("plotly_click", "overall_cell_proportions_barplot")
      # })
      #
      # plotly_server(
      #   "barplot",
      #   plot_tbl       = barplot_tbl,
      #   plot_eventdata = barplot_eventdata,
      #   group_tbl      = shiny::reactive(cohort_obj()$group_tbl)
      # )
      #
      # barplot_selected_group <- shiny::reactive({
      #   shiny::req(barplot_eventdata())
      #   barplot_eventdata()$x[[1]]
      # })
      #
      # scatterplot_tbl <- shiny::reactive({
      #   shiny::req(value_tbl(), barplot_selected_group())
      #   build_ocp_scatterplot_tbl(
      #     value_tbl(),
      #     barplot_selected_group()
      #   )
      # })
      #
      # output$scatterplot <- plotly::renderPlotly({
      #   shiny::validate(shiny::need(barplot_eventdata(), "Click above plot"))
      #   shiny::req(value_tbl())
      #
      #   groups <- dplyr::pull(value_tbl(), group)
      #   shiny::validate(shiny::need(
      #     barplot_selected_group() %in% groups,
      #     "Click above barchart"
      #   ))
      #   create_scatterplot(
      #     scatterplot_tbl(),
      #
      #     source_name = "overall_cell_proportions_scatterplot",
      #     xlab = "Stromal Fraction",
      #     ylab = "Leukocyte Fraction",
      #     label_col = "label",
      #     title = barplot_selected_group(),
      #     identity_line = TRUE
      #   )
      # })
      #
      # plotly_server(
      #   "scatterplot",
      #   plot_tbl = barplot_tbl
      # )
    }
  )
}
