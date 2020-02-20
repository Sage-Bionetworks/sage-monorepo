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

create_numerical_covariate_string <- function(covariates, transformations){
    if (is.null(covariates)) return(NULL)
    covariates %>%
        as.integer() %>%
        purrr::map(get_feature_display_from_id) %>%
        purrr::map2_chr(transformations, transform_feature_string) %>%
        stringr::str_c(collapse = " + ")
}

create_categorical_covariate_string <- function(covariates){
    if (is.null(covariates)) return(NULL)
    covariates %>%
        purrr::map(get_tag_display_from_name) %>%
        stringr::str_c(collapse = " + ")
}


