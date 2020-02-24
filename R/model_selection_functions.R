#' Get Items From Numeric Covariate Output
#'
#' @param output A named list of named lists
#' @param name A name in all of the named lists
#' @importFrom magrittr %>%
#' @importFrom purrr pluck map map_lgl discard
get_items_from_numeric_covariate_output <- function(output, name){
    output %>%
        purrr::discard(purrr::map_lgl(., is.null)) %>%
        purrr::map(purrr::pluck, name) %>%
        unlist() %>%
        unname()
}

#' Get Names From Categorical Covariate Output
#'
#' @param output A named list of named lists
#' @importFrom magrittr %>%
#' @importFrom purrr map_lgl discard
get_names_from_categorical_covariate_output <- function(output){
    output %>%
        purrr::discard(purrr::map_lgl(., is.null)) %>%
        unlist() %>%
        unname()
}

#' Create Numerical Covariate String
#'
#' @param covs A vector of strings or NULL
#' @param translate_func A function
#' @param transform_func A function
#' @param transforms A vector of strings or NULL
#'
#' @importFrom magrittr %>%
#' @importFrom purrr map map2
create_numerical_covariate_string <- function(
    covs, transforms, translate_func, transform_func){
    if (any(is.null(covs), is.null(transforms))) return(NULL)
    covs %>%
        as.integer() %>%
        purrr::map(translate_func) %>%
        purrr::map2_chr(transforms, transform_func) %>%
        paste0(collapse = " + ")
}

#' Create Categorical Covariate String
#'
#' @param covs A vector of strings or NULL
#' @param translate_func A function
#'
#' @importFrom magrittr %>%
#' @importFrom purrr map
create_categorical_covariate_string <- function(covs, translate_func){
    if (is.null(covs)) return(NULL)
    covs %>%
        purrr::map(translate_func) %>%
        paste0(collapse = " + ")
}

#' Create Categorical Covariate String
#'
#' @param prefix_str A string
#' @param num_str A string or NULL
#' @param cat_str A string or NULL
#'
#' @importFrom magrittr %>%
#' @importFrom purrr map_lgl discard
create_covariate_string <- function(prefix_str, num_str, cat_str){
    list(prefix_str, num_str, cat_str) %>%
        purrr::discard(., purrr::map_lgl(., is.null)) %>%
        paste0(collapse = " + ")
}


