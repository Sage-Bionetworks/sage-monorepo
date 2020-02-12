#' Create Feature Named List
#'
#' @param class_ids Integers in the id column of the classes table
#' @importFrom magrittr %>%
create_feature_named_list <- function(class_ids = "all"){
    list <-
        build_feature_tbl(class_ids) %>%
        create_nested_named_list()
    return(list)
}

#' Create Nested Named List
#'
#' @param tbl A tibble with the below columns
#' @param names_col1 A column that will be the names of the top list
#' @param names_col2 A column that will be the names of the nested lists
#' @param values_col A column that will be the values of the nested lists
#' @importFrom magrittr %>%
#' @importFrom dplyr select mutate
#' @importFrom tidyr drop_na nest
#' @importFrom tibble deframe
#' @importFrom purrr map
#' @importFrom rlang .data
create_nested_named_list <- function(
    tbl,
    names_col1 = "class",
    names_col2 = "display",
    values_col = "feature"
){
    list <- tbl %>%
        dplyr::select(
            n1 = names_col1,
            n2 = names_col2,
            v  = values_col
        ) %>%
        tidyr::drop_na() %>%
        tidyr::nest(data = c(.data$n2, .data$v)) %>%
        dplyr::mutate(data = purrr::map(.data$data, tibble::deframe)) %>%
        tibble::deframe(.)
    return(list)
}


# transforms ------------------------------------------------------------------

#' Scale Tibble Value Column
#'
#' @param tbl A Tibble with column "value"
#' @param scale_method One of "Log2", "Log2 + 1", "Log10 + 1", "Log10"
scale_tbl_value_column <- function(tbl, scale_method = "None"){
    if (scale_method %in% c("Log2", "Log2 + 1", "Log10 + 1", "Log10")) {
        add_amt <- 0
        base    <- 10
        if (scale_method %in% c("Log2", "Log2 + 1")) {
            base <- 2
        }
        if (scale_method %in% c("Log10 + 1", "Log2 + 1")) {
            add_amt <- 1
        }
        tbl <- log_tbl_value_column(tbl, base, add_amt)
    } else if (scale_method == "None") {
        tbl <- tbl
    } else {
        stop("Scale method does not exist")
    }
    return(tbl)
}

#' Log Tibble Value Column
#'
#' @param tbl A Tibble with column "value"
#' @param base An integer, used as the base in log
#' @param add_amt A numeric, added to the vlaue column before logging
#' @importFrom magrittr %>%
#' @importFrom dplyr mutate filter
#' @importFrom rlang .data
log_tbl_value_column <- function(tbl, base = 10, add_amt = 0){
    tbl %>%
        dplyr::mutate(value = .data$value + add_amt) %>%
        dplyr::filter(.data$value > 0) %>%
        dplyr::mutate(value = log(.data$value, base))
}

# event data utils -------------------------------------------------------------

#' Get Values from Eventdata Dataframe
#'
#' @param eventdata Eventdata from "plotly_click" plotly::event_data
#' @param col The column to get the values from
#' @importFrom magrittr %>% extract2
#' @importFrom dplyr as_tibble
get_values_from_eventdata <- function(eventdata, col = "x"){
    eventdata %>%
        dplyr::as_tibble() %>%
        magrittr::extract2(col) %>%
        unique()
}
