ici_models_server <- function(
  id
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ici_hazard_ratio_main_server(
        "ici_hazard_ratio_main"
      )
    }
  )
}

