io_targets_server <- function(
    input,
    output,
    session,
    cohort_obj
) {

    source_files <- c(
        "R/modules/server/submodules/io_target_distributions_server.R",
        "R/modules/server/submodules/io_target_datatable_server.R",
        "R/modules/server/submodules/call_module_server.R"
    )

    for (file in source_files) {
        source(file, local = T)
    }

    shiny::callModule(
        call_module_server,
        "distributions",
        cohort_obj,
        shiny::reactive(function(cohort_obj) T),
        io_target_distributions_server
    )

    shiny::callModule(
        call_module_server,
        "datatable",
        cohort_obj,
        shiny::reactive(function(cohort_obj) T),
        io_target_datatable_server
    )
}
