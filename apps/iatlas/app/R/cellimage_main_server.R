cellimage_main_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      cohort_groups <- shiny::reactive({
        iatlas.api.client::query_tags(
          cohorts = cohort_obj()$dataset_names,
          parent_tags = cohort_obj()$group_name
        ) %>%
          dplyr::pull("tag_name")
      })

      output$select_group1_ui <- shiny::renderUI({
        shiny::req(cohort_groups())
        shiny::selectInput(
          ns("group_selected1"),
          "Select Group",
          choices = cohort_groups()
        )
      })

      output$select_group2_ui <- shiny::renderUI({
        shiny::req(cohort_groups())

        shiny::selectInput(
          ns("group_selected2"),
          "Select Group",
          choices = cohort_groups()
        )
      })

      cellimage_network_server(
        "cellimage_network1",
        shiny::reactive(cohort_obj()$dataset_names),
        shiny::reactive(input$group_selected1)
      )
      cellimage_network_server(
        "cellimage_network2",
        shiny::reactive(cohort_obj()$dataset_names),
        shiny::reactive(input$group_selected2)
      )
      cellimage_plot_server(
        "cellimage_plot1",
        shiny::reactive(cohort_obj()$dataset_names),
        shiny::reactive(input$group_selected1)
      )
      cellimage_plot_server(
        "cellimage_plot2",
        shiny::reactive(cohort_obj()$dataset_names),
        shiny::reactive(input$group_selected2)
      )

      observeEvent(input$method_link,{
        shiny::showModal(modalDialog(
          title = "Method",
          includeMarkdown("inst/markdown/methods/CellImage_method.markdown"),
          easyClose = TRUE,
          footer = NULL
        ))
      })
    }
  )
}
