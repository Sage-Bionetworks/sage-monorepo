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
