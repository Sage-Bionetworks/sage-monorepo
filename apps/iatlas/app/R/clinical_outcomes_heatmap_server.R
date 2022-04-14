clinical_outcomes_heatmap_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      output$class_selection_ui <- shiny::renderUI({
        shiny::selectInput(
          inputId  = ns("class_choice"),
          label    = "Select or Search for Variable Class",
          choices  = cohort_obj()$get_feature_class_list(),
          selected = "T Helper Cell Score"
        )
      })

      output$time_feature_selection_ui <- shiny::renderUI({
        shiny::selectInput(
          inputId = ns("time_feature_choice"),
          label = "Select or Search for Survival Endpoint",
          choices = build_co_survival_list(cohort_obj()$feature_tbl)
        )
      })

      status_feature_choice <- shiny::reactive({
        shiny::req(input$time_feature_choice)
        get_co_status_feature(input$time_feature_choice)
      })

      survival_value_tbl <- shiny::reactive({
        shiny::req(input$time_feature_choice, status_feature_choice())
        build_co_survival_value_tbl(
          cohort_obj(),
          input$time_feature_choice,
          status_feature_choice()
        )
      })

      feature_tbl <- shiny::reactive({
        shiny::req(input$class_choice)
        build_co_feature_tbl(cohort_obj(), input$class_choice)
      })

      heatmap_tbl <- shiny::reactive({
        shiny::req(survival_value_tbl(), feature_tbl())
        build_co_heatmap_tbl(survival_value_tbl(), feature_tbl())
      })

      output$heatmap <- plotly::renderPlotly({
        shiny::req(heatmap_tbl())

        heatmap_matrix <- build_co_heatmap_matrix(heatmap_tbl())

        shiny::validate(shiny::need(
          nrow(heatmap_matrix > 0) & ncol(heatmap_matrix > 0),
          "No results to display, pick a different group."
        ))

        create_heatmap(heatmap_matrix, "clinical_outcomes_heatmap")
      })

      heatmap_eventdata <- shiny::reactive({
        shiny::req(heatmap_tbl())
        eventdata <- plotly::event_data("plotly_click", "clinical_outcomes_heatmap")
        if(is.null(eventdata) & !is.null(input$mock_event_data)){
          eventdata <- input$mock_event_data
        }
        shiny::validate(shiny::need(eventdata, "Click on above heatmap."))
        return(eventdata)
      })

      group_data <- shiny::reactive({
        cohort_obj()$group_tbl %>%
          dplyr::mutate("description" = stringr::str_c(
            .data$short_name, ": ", .data$characteristics)
          ) %>%
          dplyr::select("group" = "short_name", "description")
      })

      iatlas.modules::plotly_server(
        "heatmap",
        plot_data  = heatmap_tbl,
        eventdata  = heatmap_eventdata,
        group_data = group_data
      )
    }
  )
}
