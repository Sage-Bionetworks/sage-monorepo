driver_associations_server <- function(
    input,
    output,
    session,
    cohort_obj
){

    source(
        "R/modules/server/submodules/univariate_driver_server.R",
        local = T
    )
    source(
        "R/modules/server/submodules/multivariate_driver_server.R",
        local = T
    )
    source(
        "R/driver_associations_functions.R",
        local = T
    )

    # This is so that the conditional panel can see the various shiny::reactives
    output$display_ud <- shiny::reactive(show_ud_submodule(cohort_obj()))
    shiny::outputOptions(output, "display_ud", suspendWhenHidden = FALSE)
    output$display_md <- shiny::reactive(show_md_submodule(cohort_obj()))
    shiny::outputOptions(output, "display_md", suspendWhenHidden = FALSE)

    shiny::callModule(
        univariate_driver_server,
        "univariate_driver",
        cohort_obj
    )

    shiny::callModule(
        multivariate_driver_server,
        "multivariate_driver",
        cohort_obj
    )
}



