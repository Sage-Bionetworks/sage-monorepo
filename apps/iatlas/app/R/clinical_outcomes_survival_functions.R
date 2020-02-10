#' Get Survival Status ID from Time ID
#'
#' @param time_id An integer of a time status feature
#' @importFrom magrittr %>%
#' @importFrom stringr str_remove
get_status_id_from_time_id <- function(time_id){
    time_id %>%
        get_feature_display_from_id() %>%
        stringr::str_remove(., " Time") %>%
        get_feature_id_from_display()
}

#' Build Survival Values Tibble
#'
#' @param sample_tbl A Tibble with columns sample_id and group
#' @param time_id An integer in the id column of the samples table
#' @param status_id An integer in the id column of the samples table
#' @importFrom magrittr %>%
#' @importFrom dplyr inner_join select
#' @importFrom rlang .data
build_survival_values_tbl <- function(sample_tbl, time_id, status_id) {
    tbl <-
        build_clincal_outcomes_survival_tbl(time_id, status_id) %>%
        dplyr::inner_join(sample_tbl, by = "sample_id") %>%
        dplyr::select(.data$group, .data$time, .data$status)
}
