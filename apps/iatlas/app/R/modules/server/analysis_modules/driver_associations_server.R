driver_associations_server <- function(
    input,
    output,
    session,
    cohort_obj
){

    source_files <- c(
        "R/modules/server/submodules/univariate_driver_server.R",
        "R/modules/server/submodules/multivariate_driver_server.R",
        "R/modules/server/submodules/call_module_server.R",
        "R/driver_associations_functions.R"
    )

    for (file in source_files) {
        source(file, local = T)
    }

    shiny::callModule(
        call_module_server,
        "univariate_driver",
        cohort_obj,
        shiny::reactive(show_ud_submodule),
        univariate_driver_server
    )

    shiny::callModule(
        call_module_server,
        "multivariate_driver",
        cohort_obj,
        shiny::reactive(show_md_submodule),
        multivariate_driver_server
    )
}



