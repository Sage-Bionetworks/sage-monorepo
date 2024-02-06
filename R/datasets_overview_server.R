datasets_overview_server <- function(
    id,
    cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ici_overview_server(
        "ici_overview",
        cohort_obj()
      )


    }
  )
}
