

#' Get Valid Tag Filters
#'
#' @param filter_obj A list of named lists with names ids and name
#' @importFrom magrittr %>%
#' @importFrom purrr keep map_lgl
get_valid_tag_filters <- function(filter_obj){
    filter_obj %>%
        purrr::keep(purrr::map_lgl(., iatlas.app::is_tag_filter_valid)) %>%
        unname()
}

#' Is Tag Filter Valid
#'
#' @param obj A named list with names
is_tag_filter_valid <- function(obj){
    all(
        !is.null(obj),
        !is.null(obj$tags)
    )
}

#' Get Filtered Tag Samples
#'
#' @param filter_obj A list of named lists with names ids and name
#' @param samples A vector of strings
#' @importFrom magrittr %>%
#' @importFrom purrr transpose pluck map reduce
get_filtered_tag_samples <- function(filter_obj, samples){
    filter_obj %>%
        purrr::transpose(.) %>%
        purrr::pluck("tags") %>%
        purrr::map(., iatlas.app::get_filtered_group_sample_ids_by_filter) %>%
        purrr::reduce(base::intersect, .init = samples)
}

#' Get Filtered Group Sample IDs By Filter
#'
#' @param ids A vector on integers that are in the sample_id column of
#' samples_to_tags table
#' @importFrom magrittr %>%
#' @importFrom dplyr pull
get_filtered_group_sample_ids_by_filter <- function(ids){
    paste0(
        "SELECT sample_id FROM samples_to_tags WHERE tag_id IN (",
        numeric_values_to_query_list(ids),
        ")"
    ) %>%
        perform_query("get sample ids") %>%
        dplyr::pull("sample_id")
}


#' Get Valid Numeric Filters
#'
#' @param filter_obj A list of named lists with names id max and min
#' @importFrom magrittr %>%
#' @importFrom purrr keep map_lgl
get_valid_numeric_filters <- function(filter_obj){
    filter_obj %>%
        purrr::keep(purrr::map_lgl(., is_numeric_filter_valid)) %>%
        unname()
}

#' Is Numeric Filter Valid
#'
#' @param obj A named lists with names id max and min
is_numeric_filter_valid <- function(obj){
    all(
        !is.null(obj),
        !any(is.null(obj$id), is.null(obj$min), is.null(obj$max)),
        all(names(obj) %in% c("id", "min", "max"))
    )
}

#' Get Filtered Feature Sample IDs
#'
#' @param filter_obj A list of named lists with names id max and min
#' @param sample_ids A vector of integers in the sample_id column of the
#' features_to_samples table
#' @importFrom magrittr %>%
#' @importFrom purrr transpose reduce
get_filtered_feature_sample_ids <- function(filter_obj, sample_ids){
    filter_obj %>%
        purrr::transpose(.) %>%
        purrr::pmap(., get_filtered_feature_sample_ids_by_filter) %>%
        purrr::reduce(base::intersect, .init = sample_ids)
}

#' Get Filtered Feature Sample IDs By Filter
#'
#' @param id An integer in the smaple_id column of the
#' features_to_samples table
#' @param min A numeric
#' @param max A numeric
#' @importFrom magrittr %>%
#' @importFrom dplyr pull
get_filtered_feature_sample_ids_by_filter <- function(id, min, max){
    paste0(
        "SELECT sample_id FROM features_to_samples ",
        "WHERE value <= ",  max, " ",
        "AND value >=", min, " ",
        "AND feature_id = ", id
    ) %>%
        perform_query("Get Filtered Feature Sample IDs") %>%
        dplyr::pull("sample_id")
}

#' Create Cohort Filter Object
#'
#' @param sample_ids A vector of integers
#' @param numeric_obj A list
#' @param group_obj A list
create_cohort_filter_object <- function(sample_ids, numeric_obj, group_obj){
    list(
        "sample_ids" = sample_ids,
        "filters" = list(
            "feature_filters" = numeric_obj,
            "group_filters" = group_obj
        )
    )
}


