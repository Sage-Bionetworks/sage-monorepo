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



