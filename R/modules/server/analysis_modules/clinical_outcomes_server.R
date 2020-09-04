clinical_outcomes_server <- function(
    input,
    output,
    session,
    cohort_obj
){

    source_files <- c(
        "R/modules/server/submodules/clinical_outcomes_survival_server.R",
        "R/modules/server/submodules/clinical_outcomes_heatmap_server.R",
        "R/modules/server/submodules/call_module_server.R"
    )

    for (file in source_files) {
        source(file, local = T)
    }

    call_module_server(
      "clinical_outcomes_survival",
      cohort_obj,
      shiny::reactive(show_co_submodules),
      clinical_outcomes_survival_server
    )

    call_module_server(
      "clinical_outcomes_heatmap",
      cohort_obj,
      shiny::reactive(show_co_submodules),
      clinical_outcomes_heatmap_server
    )
}


