cohort_dataset_selection_server <- function(
  id,
  default_dataset = "TCGA"
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {
      ns <- session$ns
      choices = iatlas.api.client::query_datasets(types = "analysis") %>%
        dplyr::select("display", "name") %>%
        tibble::deframe(.)

      output$dataset_selection_ui <- shiny::renderUI({
        shiny::selectInput(
          inputId  = ns("dataset_choice"),
          label    = "Select or Search for Dataset",
          choices  = choices,
          selected = default_dataset
        )
      })

      output$module_availibility_string <- shiny::renderText({
        shiny::req(input$dataset_choice)
        create_cohort_module_string(
          input$dataset_choice
        )
      })

      return(shiny::reactive(input$dataset_choice))
    }
  )
}
