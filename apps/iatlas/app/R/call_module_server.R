call_module_server <- function(
  id,
  cohort_obj,
  server_function,
  ui_function,
  test_function = shiny::reactive(function(cohort_obj) T),
  warning_message = "Currently selected cohort does not have the needed features to display this section",
  ...
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      display_module <- shiny::reactive({
        shiny::req(cohort_obj(), test_function())
        test_function()(cohort_obj())
      })

      output$ui <- shiny::renderUI({
        shiny::req(!is.null(display_module()))
        if(display_module()){
          server_function("module", cohort_obj, ...)
          ui_function(ns("module"))
        } else {
          iatlas.modules::textBox(width = 12, warning_message)
        }
      })
    }
  )
}
