create_group_named_list <- function(tbl){
    "SELECT display, id FROM tags" %>%
        perform_query("Get tags") %>%
        dplyr::inner_join(tbl, by = c("display" = "group")) %>%
        dplyr::select("display", "id") %>%
        tibble::deframe(.)
}

get_filtered_group_sample_ids <- function(filter_obj, sample_ids){
    filter_obj %>%
        purrr::discard(purrr::map_lgl(., is.null)) %>%
        purrr::flatten(.) %>%
        purrr::map(get_filtered_group_sample_ids_by_filter) %>%
        purrr::reduce(base::intersect, .init = sample_ids)
}

get_filtered_group_sample_ids_by_filter <- function(group_ids){
    paste0(
        "SELECT sample_id FROM samples_to_tags WHERE tag_id IN (",
        numeric_values_to_query_list(group_ids),
        ")"
    ) %>%
        perform_query("get sample ids") %>%
        dplyr::pull(.data$sample_id)
}

get_filtered_feature_sample_ids <- function(filter_obj, sample_ids){
    filter_obj %>%
        purrr::discard(purrr::map_lgl(., is.null)) %>%
        purrr::discard(purrr::map_lgl(., ~is.null(.x$feature_range))) %>%
        purrr::map(purrr::flatten) %>%
        purrr::map(unname) %>%
        purrr::map(., ~rlang::exec(
            get_filtered_feature_sample_ids_by_filter, !!!.x
        )) %>%
        purrr::reduce(base::intersect, .init = sample_ids)
}

#' Get Filtered Feature Sample IDs By Filter
#'
#' @param feature_id An integer in the smaple_id column of the
#' features_to_samples table
#' @param min A numeric
#' @param max A numeric
#' @importFrom magrittr %>%
#' @importFrom dplyr pull
get_filtered_feature_sample_ids_by_filter <- function(feature_id, min, max){
    paste0(
        "SELECT sample_id FROM features_to_samples ",
        "WHERE value <= ",  max, " ",
        "AND value >=", min, " ",
        "AND feature_id = ", feature_id
    ) %>%
        perform_query("Get Filtered Feature Sample IDs") %>%
        dplyr::pull(.data$sample_id)
}


