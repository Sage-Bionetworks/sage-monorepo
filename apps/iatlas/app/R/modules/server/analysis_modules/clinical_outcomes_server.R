clinical_outcomes_server <- function(
    input,
    output,
    session,
    cohort_obj
){
    source(
        "R/modules/server/submodules/clinical_outcomes_survival_server.R",
        local = T
    )
    source(
        "R/modules/server/submodules/clinical_outcomes_heatmap_server.R",
        local = T
    )

    shiny::callModule(
        clinical_outcomes_survival_server,
        "clinical_outcomes_survival",
        cohort_obj
    )

    shiny::callModule(
        clinical_outcomes_heatmap_server,
        "clinical_outcomes_heatmap",
        cohort_obj
    )
}


