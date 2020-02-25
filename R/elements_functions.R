#' Build Numeric Filter Tibble
#'
#' @param feature_id An integer in the feature_id column of the
#' features_to_samples table
#' @importFrom magrittr %>%
#' @importFrom dplyr summarise
#' @importFrom rlang .data
build_numeric_filter_tbl <- function(feature_id){
    feature_id %>%
        build_feature_value_tbl_from_ids() %>%
        dplyr::summarise(
            feature_max = max(.data$value),
            feature_min = min(.data$value)
        )
}

#' Build Group Filter Tibble
#'
#' @param feature_id An integer in the related_tag_id column of the
#' tags_to_tags table
#' @importFrom magrittr %>%
#' @importFrom tibble deframe
build_group_filter_tbl <- function(feature_id){
    feature_id %>%
        create_parent_group_query_from_id() %>%
        paste("SELECT name, id FROM  (", ., ") a") %>%
        perform_query("Build Groups Tibble") %>%
        tibble::deframe(.)
}
