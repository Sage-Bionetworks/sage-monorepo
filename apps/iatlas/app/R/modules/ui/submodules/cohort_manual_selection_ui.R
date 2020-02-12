cohort_manual_selection_ui <- function(id) {

    ns <- shiny::NS(id)

    source("R/modules/ui/submodules/cohort_group_selection_ui.R", local = T)
    source("R/modules/ui/submodules/cohort_filter_selection_ui.R", local = T)
    source("R/modules/ui/submodules/cohort_dataset_selection_ui.R", local = T)

    .GlobalEnv$sectionBox(
        title = "Cohort Selection",
        .GlobalEnv$messageBox(
            width = 12,
            shiny::p("Group Selection and filtering"),
        ),
        cohort_dataset_selection_ui(ns("cohort_dataset_selection")),
        cohort_filter_selection_ui(ns("cohort_filter_selection")),
        cohort_group_selection_ui(ns("cohort_group_selection"))
    )
}
