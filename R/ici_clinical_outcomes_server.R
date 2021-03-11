ici_clinical_outcomes_server <- function(
  id
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      #ici_data <- load_io_data()

      ici_clinical_outcomes_plot_server(
        "ici_clinical_outcomes_plot"
      )
    }
  )
}
