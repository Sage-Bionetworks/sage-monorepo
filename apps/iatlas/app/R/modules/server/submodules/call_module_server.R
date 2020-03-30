call_module_server <- function(
    input,
    output,
    session,
    cohort_obj,
    test_function,
    server_function,
    ...
) {
    # This is so that the conditional panel can see the various shiny::reactives
    output$display_module <- shiny::reactive(test_function()(cohort_obj()))
    shiny::outputOptions(output, "display_module", suspendWhenHidden = FALSE)

    shiny::callModule(
        server_function,
        "module",
        cohort_obj,
        ...
    )
}
