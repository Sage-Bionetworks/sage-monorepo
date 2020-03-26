#' Create Cohort Module String
#'
#' @param tbl A tibble with columns dataset, and module
#' @param .dataset a string in the dataset column of the tibble
#' @importFrom magrittr %>%
#' @importFrom rlang .data
#' @importFrom dplyr filter pull
create_cohort_module_string <- function(tbl, .dataset){
    tbl %>%
        dplyr::filter(.data$dataset == .dataset) %>%
        dplyr::pull(.data$module) %>%
        paste0(collapse = ", ") %>%
        paste0(
            "Modules available for dataset ",
            .dataset,
            ": ",
            .
        )
}
