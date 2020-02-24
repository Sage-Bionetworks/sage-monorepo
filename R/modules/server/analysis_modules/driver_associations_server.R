driver_associations_server <- function(
    input,
    output,
    session,
    sample_tbl,
    group_name
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
        group_name
    )

    shiny::callModule(
        multivariate_driver_server,
        "multivariate_driver",
        sample_tbl,
        group_name
    )
}



