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

build_co_survival_list <- function(tbl){
   tbl %>%
        dplyr::filter(.data$class == "Survival Time") %>%
        dplyr::arrange(.data$order) %>%
        dplyr::select("display", "name") %>%
        tibble::deframe(.)
}

get_co_status_feature <- function(time_feature){
    if (time_feature == "PFI_time_1") return("PFI_1")
    else if (time_feature == "OS_time") return("OS")
    else stop("Uknown time feature")
}


#' Build Survival Values Tibble
#'
#' @param sample_tbl A Tibble with columns sample_id and group
#' @param time_id An integer in the id column of the samples table
#' @param status_id An integer in the id column of the samples table
#' @importFrom magrittr %>%
#' @importFrom dplyr inner_join select
#' @importFrom rlang .data
build_co_survival_value_tbl <- function(cohort_obj, time, status) {
    time_tbl <-
        query_feature_values_with_cohort_object(cohort_obj, time) %>%
        dplyr::select("sample", "time" = "value")
    status_tbl <-
        query_samples_to_feature(status) %>%
        dplyr::select("sample", "status" = "value")
    tbl <-
        purrr::reduce(
            list(cohort_obj$sample_tbl, time_tbl, status_tbl),
            dplyr::inner_join,
            by = "sample"
        ) %>%
        dplyr::select("sample", "group", "time", "status")
    return(tbl)
}

build_co_feature_tbl <- function(cohort_obj, feature_class){
    cohort_obj %>%
        query_feature_values_with_cohort_object(class = feature_class) %>%
        dplyr::select(
            "sample",
            "feature_name" = "display",
            "feature_value" = "value",
            "feature_order" = "order"
        )
}

build_co_heatmap_tbl <- function(survival_tbl, feature_tbl, sample_tbl){
    survival_tbl %>%
        dplyr::select(-"group") %>%
        dplyr::inner_join(feature_tbl, by = "sample") %>%
        dplyr::inner_join(sample_tbl, by = "sample") %>%
        dplyr::select(
            "sample",
            "group",
            "time",
            "status",
            "feature_name",
            "feature_value",
            "feature_order"
        )
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
            "feature" = "feature_name",
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
        dplyr::select(sort(names(.))) %>%
        tibble::column_to_rownames("feature") %>%
        as.matrix()
}
