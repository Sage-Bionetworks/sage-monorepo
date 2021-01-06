ici_overview_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ici_data <- load_io_data()

      call_module_server(
        "ici_overview_datasets",
        cohort_obj,
        ioresponse_data = ici_data,
        server_function = ici_overview_datasets_server,
        ui_function = ici_overview_datasets_ui
      )

      call_module_server(
        "ici_overview_category",
        cohort_obj,
        server_function = ici_overview_category_server,
        ui_function = ici_overview_category_ui
      )
    }
  )
}
