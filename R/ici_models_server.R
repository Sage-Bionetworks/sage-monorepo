ici_models_server <- function(
  id
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ici_models_train_server(
        "ici_models_train"
      )
    }
  )
}

