cohort_manual_selection_server <- function(
    input,
    output,
    session
){

    source(
        "R/modules/server/submodules/cohort_group_selection_server.R",
        local = T
    )
    source(
        "R/modules/server/submodules/cohort_filter_selection_server.R",
        local = T
    )
    source(
        "R/modules/server/submodules/cohort_dataset_selection_server.R",
        local = T
    )

    default_dataset <- "TCGA"

    selected_dataset <- shiny::callModule(
        cohort_dataset_selection_server,
        "cohort_dataset_selection",
        default_dataset
    )

    dataset <- shiny::reactive({
        if (is.null(selected_dataset())) {
            return(default_dataset)
        } else {
            return(selected_dataset())
        }
    })

    all_sample_ids <- shiny::reactive({
        shiny::req(dataset())
        stringr::str_c(
            "SELECT sample_id FROM datasets_to_samples WHERE dataset_id IN(",
            "SELECT id FROM datasets WHERE name = '", dataset(), "'",
            ")"
        ) %>%
            perform_query() %>%
            dplyr::pull("sample_id")
    })

    filter_obj <- cohort_obj <- shiny::callModule(
        cohort_filter_selection_server,
        "cohort_filter_selection",
        dataset,
        all_sample_ids
    )

    cohort_obj <- shiny::callModule(
        cohort_group_selection_server,
        "cohort_group_selection",
        filter_obj,
        dataset
    )

    return(cohort_obj)
}
