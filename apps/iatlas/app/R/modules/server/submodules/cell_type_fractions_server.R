cell_type_fractions_server <- function(id, cohort_obj) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      value_tbl <- shiny::reactive({
        shiny::req(input$fraction_group_choice)
        build_ctf_value_tbl(cohort_obj(), input$fraction_group_choice)
      })

      plot_tbl <- shiny::reactive({
        shiny::req(value_tbl())
        iatlas.app::build_ctf_barplot_tbl(value_tbl())
      })

      output$barplot <- plotly::renderPlotly({
        shiny::req(plot_tbl())

        iatlas.app::create_barplot(
          plot_tbl(),
          source_name = "cell_type_fractions_barplot",
          color_col = "color",
          label_col = "label",
          xlab = "Fraction type by group",
          ylab = "Fraction mean"
        )
      })

      barplot_eventdata <- shiny::reactive({
        plotly::event_data("plotly_click", "cell_type_fractions_barplot")
      })

      plotly_server(
        "barplot",
        plot_tbl       = plot_tbl,
        plot_eventdata = barplot_eventdata,
        group_tbl      = shiny::reactive(cohort_obj()$group_tbl)
      )
    }
  )
}
