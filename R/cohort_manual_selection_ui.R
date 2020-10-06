cohort_manual_selection_ui <- function(id) {

    ns <- shiny::NS(id)

    shiny::tagList(
        cohort_dataset_selection_ui(ns("dataset_selection")),
        cohort_filter_selection_ui(ns("filter_selection")),
        cohort_group_selection_ui(ns("group_selection"))
    )
}
