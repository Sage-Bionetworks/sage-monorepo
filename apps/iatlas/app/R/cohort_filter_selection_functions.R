#' Is Tag Filter Valid
#'
#' @param obj A named list with names
is_tag_filter_valid <- function(obj){
    all(
        !is.null(obj),
        !is.null(obj$tags)
    )
}

#' Get Valid Tag Filters
#'
#' @param filter_obj A list of named lists with names ids and name
#' @importFrom magrittr %>%
#' @importFrom purrr keep map_lgl
get_valid_tag_filters <- function(filter_obj){
    filter_obj %>%
        purrr::keep(purrr::map_lgl(., is_tag_filter_valid)) %>%
        unname()
}

#' Get Filtered Tag Samples
#'
#' @param filter_obj A list of named lists with names ids and name
#' @param samples A vector of strings
#' @importFrom magrittr %>%
#' @importFrom purrr transpose pluck map reduce
get_filtered_tag_samples <- function(filter_obj, samples, dataset){
    filter_obj %>%
        purrr::transpose(.) %>%
        purrr::pluck("tags") %>%
        purrr::map(., ~iatlas.api.client::query_tag_samples(datasets = dataset, tags = .x)) %>%
        purrr::map(., dplyr::pull, "sample") %>%
        purrr::reduce(base::intersect, .init = samples)
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
        !any(is.null(obj$feature), is.null(obj$min), is.null(obj$max)),
        all(names(obj) %in% c("feature", "min", "max"))
    )
}

#' Get Filtered Feature Sample IDs
#'
#' @param filter_obj A list of named lists with names id max and min
#' @param sample_ids A vector of integers in the sample_id column of the
#' features_to_samples table
#' @importFrom magrittr %>%
#' @importFrom purrr transpose reduce
get_filtered_feature_samples <- function(filter_obj, samples, dataset){
    filter_obj %>%
        purrr::transpose(.) %>%
        purrr::map(~unlist(.x)) %>%
        purrr::pmap(., get_filtered_samples_by_feature, dataset) %>%
        purrr::reduce(base::intersect, .init = samples)
}

get_filtered_samples_by_feature <- function(feature, min, max, dataset){
    iatlas.api.client::query_feature_values(feature, dataset, max_value = max, min_value = min) %>%
        dplyr::pull(sample)
}
