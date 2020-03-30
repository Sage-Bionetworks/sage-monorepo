tumor_microenvironment_server <- function(
    input,
    output,
    session,
    cohort_obj
) {

    source_files <- c(
        "R/modules/server/submodules/overall_cell_proportions_server.R",
        "R/modules/server/submodules/cell_type_fractions_server.R",
        "R/modules/server/submodules/call_module_server.R",
        "R/tumor_microenvironment_functions.R"
    )

    for (file in source_files) {
        source(file, local = T)
    }

    shiny::callModule(
        call_module_server,
        "overall_cell_proportions",
        cohort_obj,
        shiny::reactive(show_ocp_submodule),
        overall_cell_proportions_server
    )

    shiny::callModule(
        call_module_server,
        "cell_type_fractions",
        cohort_obj,
        shiny::reactive(show_ctf_submodule),
        cell_type_fractions_server
    )
}
