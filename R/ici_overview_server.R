ici_overview_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ioresponse_data <- load_io_data()

      ici_overview_datasets_server(
        "ici_overview_datasets",
        ioresponse_data,
        shiny::reactive(input$data_group)
      )

      ici_overview_category_server(
        "ici_overview_category",
        ioresponse_data,
        shiny::reactive(input$data_group)
      )

    }
  )
}
