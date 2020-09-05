call_module_server <- function(
  id,
  cohort_obj,
  server_function,
  test_function = shiny::reactive(function(cohort_obj) T),
  ...
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {
      # This is so that the conditional panel can see the various shiny::reactives

      display_module <- shiny::reactive({
        shiny::req(cohort_obj(), test_function())
        test_function()(cohort_obj())
      })

      output$display_module <- shiny::reactive(display_module())

      shiny::outputOptions(output, "display_module", suspendWhenHidden = FALSE)

      server_function(
        "module",
        cohort_obj,
        ...
      )
    }
  )
}
