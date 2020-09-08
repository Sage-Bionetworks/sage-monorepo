immune_feature_distributions_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      output$selection_ui <- shiny::renderUI({
        choices <-  create_nested_named_list(
          cohort_obj()$feature_tbl, values_col = "name"
        )
        shiny::selectInput(
          inputId  = ns("feature_choice_name"),
          label    = "Select or Search for Variable",
          selected = "leukocyte_fraction",
          choices  = choices
        )
      })

      feature_choice_display <- shiny::reactive({
        shiny::req(input$feature_choice_name)
        cohort_obj()$feature_tbl %>%
          dplyr::filter(name == input$feature_choice_name) %>%
          dplyr::pull(display)
      })

      feature_plot_label <- shiny::reactive({
        shiny::req(input$scale_method_choice, feature_choice_display())
        transform_feature_string(
          feature_choice_display(),
          input$scale_method_choice
        )
      })

      distplot_tbl <- shiny::reactive({
        shiny::req(
          input$feature_choice_name,
          input$scale_method_choice
        )
        build_ifd_distplot_tbl(
          cohort_obj(),
          input$feature_choice_name,
          input$scale_method_choice
        )
      })

      distribution_plot_server(
        "immune_feature_dist_plot",
        cohort_obj,
        distplot_tbl    = distplot_tbl,
        distplot_type   = shiny::reactive(input$plot_type_choice),
        distplot_ylab   = feature_plot_label,
        distplot_title  = feature_choice_display
      )
    }
  )
}
