clinical_outcomes_survival_server <- function(id, cohort_obj) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      output$time_feature_selection_ui <- shiny::renderUI({
        shiny::selectInput(
          inputId = ns("time_feature_choice"),
          label = "Select or Search for Survival Endpoint",
          choices = build_co_survival_list(
            cohort_obj()$feature_tbl
          )
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

      output$survival_plot <- shiny::renderPlot({

        shiny::req(survival_value_tbl(), input$risktable)

        shiny::validate(shiny::need(
          nrow(survival_value_tbl()) > 0,
          paste0(
            "Samples with selected variable don't have selected ",
            "survival features."
          )
        ))

        num_groups <- length(unique(survival_value_tbl()$group))

        shiny::validate(shiny::need(
          num_groups <= 10,
          paste0(
            "Too many sample groups (", num_groups, ") ",
            "for KM plot; choose a continuous variable or select ",
            "different sample groups."
          )
        ))

        fit <- survival::survfit(
          survival::Surv(time, status) ~ group,
          data = survival_value_tbl()
        )

        create_kmplot(
          fit = fit,
          df = survival_value_tbl(),
          confint = input$confint,
          risktable = input$risktable,
          title = cohort_obj()$group_name,
          group_colors = unname(cohort_obj()$plot_colors)
        )
      })

      output$download_tbl <- shiny::downloadHandler(
        filename = function() stringr::str_c("data-", Sys.Date(), ".csv"),
        content = function(con) readr::write_csv(survival_value_tbl(), con)
      )
    }
  )
}
