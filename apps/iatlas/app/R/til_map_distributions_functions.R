#' Create Tilmap Named List
#'
#' @param tbl A tibble with columns class, display and id
#' @importFrom magrittr %>%
#' @importFrom rlang .data
#' @importFrom dplyr select filter
#' @importFrom tibble deframe
create_tm_named_list <- function(cohort_obj){
    cohort_obj %>%
        purrr::pluck("feature_tbl") %>%
        dplyr::filter(.data$class %in% "TIL Map Characteristic") %>%
        dplyr::select("display", "name") %>%
        tibble::deframe(.)
}

#' Build Tilmap Distplot Tibble
#'
#' @param tbl A tibble with columns sample_id, sample_name, slide_barcode, group
#' @param id An integer in the feature_id column of the features_to_samples
#' table
#' @param scale_method A string
#' @importFrom magrittr %>%
#' @importFrom rlang .data
#' @importFrom dplyr select rename inner_join
build_tm_distplot_tbl <- function(cohort_object, feature_name, scale_method){
    cohort_object %>%
        query_feature_values_with_cohort_object(feature = feature_name) %>%
        dplyr::inner_join(cohort_object$sample_tbl, by = "sample") %>%
        dplyr::select("group", "value" = "feature_value") %>%
        scale_tbl_value_column(scale_method) %>%
        dplyr::rename(x = .data$group, y = .data$value)
}
