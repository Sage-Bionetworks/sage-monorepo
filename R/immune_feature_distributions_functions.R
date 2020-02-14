#' Build Immune Feature Distribution Plot Tibble
#'
#' @param sample_tbl A tibble with columns "sample_id", "group"
#' @param feature_id An integer in the features table
#' @param scale_method a method for scaling the value column
#' @importFrom magrittr %>%
#' @importFrom dplyr inner_join select rename
#' @importFrom rlang .data
build_ifd_distplot_tbl <- function(sample_tbl, feature_id, scale_method){
    feature_id %>%
        build_feature_value_tbl_from_ids() %>%
        dplyr::inner_join(sample_tbl, by = "sample_id") %>%
        dplyr::select(.data$group, .data$value) %>%
        scale_tbl_value_column(scale_method) %>%
        dplyr::rename(x = .data$group, y = .data$value)
}

#' Build Immune Feature Distribution Histogram Tibble
#'
#' @param distplot_tbl A tibble with columns "x", "y"
#' @param group A string whoose value is in the x column
#' @importFrom magrittr %>%
#' @importFrom dplyr pull
#' @importFrom rlang .data
build_ifd_histplot_tbl <- function(distplot_tbl, group){
    distplot_tbl %>%
        dplyr::filter(.data$x == group) %>%
        dplyr::select(x = .data$y)
}
