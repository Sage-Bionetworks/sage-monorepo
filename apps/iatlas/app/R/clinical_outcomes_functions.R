show_co_submodules <- function(cohort_obj){
    time_features <- cohort_obj %>%
        purrr::pluck("feature_tbl") %>%
        dplyr::filter(.data$class %in% "Survival Time") %>%
        dplyr::pull(.data$display)

    status_features <- cohort_obj %>%
        purrr::pluck("feature_tbl") %>%
        dplyr::filter(.data$class %in% "Survival Status") %>%
        dplyr::pull(.data$display)

    all(length(time_features > 0), length(status_features > 0))
}


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
build_survival_value_tbl <- function(sample_tbl, time_feature, status_feature) {
    time_tbl <-
        iatlas.app::query_samples_to_feature(time_feature) %>%
        dplyr::rename("time" = "value")
    status_tbl <-
        iatlas.app::query_samples_to_feature(status_feature) %>%
        dplyr::rename("status" = "value")
    tbl <-
        purrr::reduce(
            list(sample_tbl, time_tbl, status_tbl),
            dplyr::inner_join,
            by = "sample"
        ) %>%
        dplyr::select(.data$sample, .data$group, .data$time, .data$status)
    return(tbl)

}

#' Build Heatmap Matrix
#'
#' @param tbl A tibble with columns feature, value, time, status, group
#' @importFrom magrittr %>%
#' @importFrom dplyr select mutate
#' @importFrom tidyr nest pivot_wider
#' @importFrom tibble column_to_rownames
#' @importFrom purrr map2_dbl map
#' @importFrom concordanceIndex concordanceIndex
#' @importFrom rlang .data
build_co_heatmap_matrix <- function(tbl){
    tbl %>%
        dplyr::select(
            "feature" = "feature_display",
            "value" = "feature_value",
            "time",
            "status",
            "group",
            "order" = "feature_order"
        ) %>%
        tidyr::nest(
            value = .data$value,
            data = c(.data$time, .data$status)
        ) %>%
        dplyr::mutate(
            value = purrr::map(.data$value, as.matrix),
            data = purrr::map(.data$data, as.matrix)
        ) %>%
        dplyr::mutate(result = purrr::map2_dbl(
            .data$value,
            .data$data,
            concordanceIndex::concordanceIndex
        )) %>%
        dplyr::select("feature", "group", "result") %>%
        tidyr::pivot_wider(
            .data$feature,
            names_from = .data$group,
            values_from = .data$result
        ) %>%
        as.data.frame() %>%
        tibble::column_to_rownames("feature") %>%
        as.matrix()
}

#' Build Clinical Outcomes Survival Tibble
#'
#' @param time_id An integer in the id column of the samples table
#' @param status_id An integer in the id column of the samples table
build_co_survival_tbl <- function(time_id, status_id){
    paste0(
        "SELECT a.sample_id, a.time, b.status FROM (",
        paste0(
            "SELECT a.sample_id, a.value AS time ",
            "FROM features_to_samples a ",
            "INNER JOIN features f ON a.feature_id = f.id ",
            "WHERE feature_id = ",
            time_id
        ),
        ") a INNER JOIN (",
        paste0(
            "SELECT a.sample_id, a.value AS status ",
            "FROM features_to_samples a ",
            "INNER JOIN features f ON a.feature_id = f.id ",
            "WHERE feature_id = ",
            status_id
        ),
        ") b ON a.sample_id = b.sample_id"
    )  %>%
        perform_query("Build Clinical Outcomes Survival Tibble")
}
