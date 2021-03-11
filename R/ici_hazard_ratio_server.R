ici_hazard_ratio_server <- function(
  id
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      # ici_data <- load_io_data()

      ici_hazard_ratio_main_server(
        "ici_hazard_ratio_main"
      )
    }
  )
}
