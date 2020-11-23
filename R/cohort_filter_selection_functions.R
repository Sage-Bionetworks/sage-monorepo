
is_tag_filter_valid <- function(obj){
    all(
        !is.null(obj),
        !is.null(obj$tags)
    )
}

get_valid_tag_filters <- function(filter_obj){
    filter_obj %>%
        purrr::keep(purrr::map_lgl(., is_tag_filter_valid)) %>%
        unname()
}

get_filtered_tag_samples <- function(filter_obj, samples, dataset){
    filter_obj %>%
        purrr::transpose(.) %>%
        purrr::pluck("tags") %>%
        purrr::map(., ~iatlas.api.client::query_samples_by_tag2(datasets = dataset, tags = .x)) %>%
        purrr::map(., dplyr::pull, "sample") %>%
        purrr::reduce(base::intersect, .init = samples)
}

get_valid_numeric_filters <- function(filter_obj){
    filter_obj %>%
        purrr::keep(purrr::map_lgl(., is_numeric_filter_valid)) %>%
        unname()
}

is_numeric_filter_valid <- function(obj){
    all(
        !is.null(obj),
        !any(is.null(obj$feature), is.null(obj$min), is.null(obj$max)),
        all(names(obj) %in% c("feature", "min", "max"))
    )
}

get_filtered_feature_samples <- function(filter_obj, samples, dataset){
    filter_obj %>%
        purrr::transpose(.) %>%
        purrr::map(~unlist(.x)) %>%
        purrr::pmap(., get_filtered_samples_by_feature, dataset) %>%
        purrr::reduce(base::intersect, .init = samples)
}

get_filtered_samples_by_feature <- function(feature, min, max, dataset){
    iatlas.api.client::query_feature_values(
      dataset, features = feature, max_value = max, min_value = min
    ) %>%
        dplyr::pull(sample)
}
