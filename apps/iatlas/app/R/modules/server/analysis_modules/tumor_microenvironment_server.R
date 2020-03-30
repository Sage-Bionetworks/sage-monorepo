tumor_microenvironment_server <- function(
    input,
    output,
    session,
    cohort_obj
) {

    source(
        "R/modules/server/submodules/overall_cell_proportions_server.R",
        local = T
    )
    source(
        "R/modules/server/submodules/cell_type_fractions_server.R",
        local = T
    )

    source(
        "R/tumor_microenvironment_functions.R",
        local = T
    )


    # This is so that the conditional panel can see the various shiny::reactives
    output$display_ocp <- shiny::reactive(show_ocp_submodule(cohort_obj()))
    shiny::outputOptions(output, "display_ocp", suspendWhenHidden = FALSE)
    output$display_ctf <- shiny::reactive(show_ctf_submodule(cohort_obj()))
    shiny::outputOptions(output, "display_ctf", suspendWhenHidden = FALSE)

    shiny::callModule(
        overall_cell_proportions_server,
        "overall_cell_proportions",
        cohort_obj
    )

    shiny::callModule(
        cell_type_fractions_server,
        "cell_type_fractions",
        cohort_obj
    )
}
