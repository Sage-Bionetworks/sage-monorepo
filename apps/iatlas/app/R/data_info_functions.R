#' Format Feature Tibble
#'
#' @param feature_tbl A tibble with columns feature, unit and class
#' @importFrom dplyr select
#' @importFrom rlang .data
format_feature_tbl <- function(feature_tbl){
    dplyr::select(
        feature_tbl,
        `Feature Name`   = .data$display,
        `Variable Class` = .data$class,
        Unit             = .data$unit
    )
}

#' Filter Feature Tibble
#'
#' @param feature_tbl A tibble with columns class
#' @param selected_row An integer that is a row number in the tibble
#' @importFrom magrittr %>%
#' @importFrom dplyr slice pull filter
#' @importFrom rlang .data
filter_feature_tbl <- function(feature_tbl, selected_row){
    selected_row_class <- feature_tbl %>%
        dplyr::slice(selected_row) %>%
        dplyr::pull(.data$class)

    dplyr::filter(feature_tbl, .data$class == selected_row_class)
}

#' Format Filtered Feature Tibble
#'
#' @param filtered_feature_tbl A tibble with columns order, display, unit, and
#' method_tag
#' @importFrom dplyr select
#' @importFrom rlang .data
format_filtered_feature_tbl <- function(filtered_feature_tbl){
    dplyr::select(
        filtered_feature_tbl,
        `Variable Class Order` = .data$order,
        `Feature Name`         = .data$display,
        Unit                   = .data$unit,
        `Methods Tag`          = .data$method_tag
    )
}

