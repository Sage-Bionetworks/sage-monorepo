#' Get Filtered Feature Sample IDs
#'
#' @param feature_id An integer in the smaple_id column of the
#' features_to_samples table
#' @param min A numeric
#' @param max A numeric
#' @importFrom magrittr %>%
#' @importFrom dplyr pull
get_filtered_feature_sample_ids <- function(feature_id, min, max){
    paste0(
        "SELECT sample_id FROM features_to_samples ",
        "WHERE value <= ",  max, " ",
        "AND value >=", min, " ",
        "AND feature_id = ", feature_id
    ) %>%
        perform_query("Get Filtered Feature Sample IDs") %>%
        dplyr::pull(.data$sample_id)
}

get_filtered_group_sample_ids <- function(group_ids){
    paste0(
        "SELECT sample_id FROM samples_to_tags WHERE tag_id IN (",
        numeric_values_to_query_list(group_ids),
        ")"
    ) %>%
        perform_query("get sample ids") %>%
        dplyr::pull(.data$sample_id)
}
