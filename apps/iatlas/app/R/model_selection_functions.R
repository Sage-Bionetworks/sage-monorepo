
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

create_numerical_covariate_string <- function(
    covs,
    transforms,
    transform_func
){
    if (any(is.null(covs), is.null(transforms))) return(NULL)
    covs %>%
        purrr::map2_chr(transforms, transform_func) %>%
        stringr::str_c(collapse = " + ")
}

create_covariate_string <- function(prefix_str, num_str, cat_str){
    list(prefix_str, num_str, cat_str) %>%
        purrr::discard(., purrr::map_lgl(., is.null)) %>%
        paste0(collapse = " + ")
}


