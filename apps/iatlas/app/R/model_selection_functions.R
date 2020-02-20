get_items_from_numeric_covariate_output <- function(output, name){
    output %>%
        purrr::discard(purrr::map_lgl(., is.null)) %>%
        purrr::map(purrr::pluck, name) %>%
        unlist() %>%
        unname()
}

get_names_from_categorical_covariate_output <- function(output){
    output %>%
        purrr::discard(purrr::map_lgl(., is.null)) %>%
        unlist() %>%
        unname()
}

create_numerical_covariate_string <- function(covs, transforms, trans_func){
    if (any(is.null(covs), is.null(transforms))) return(NULL)
    covs %>%
        as.integer() %>%
        purrr::map(get_feature_display_from_id) %>%
        purrr::map2_chr(transforms, trans_func) %>%
        stringr::str_c(collapse = " + ")
}

create_categorical_covariate_string <- function(covs){
    if (is.null(covs)) return(NULL)
    covs %>%
        purrr::map(get_tag_display_from_name) %>%
        stringr::str_c(collapse = " + ")
}


create_covariate_string <- function(prefix_str, num_str, cat_str){
    list(prefix_str, num_str, cat_str) %>%
        purrr::discard(., purrr::map_lgl(., is.null)) %>%
        stringr::str_c(collapse = " + ")
}


