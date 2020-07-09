
#' Build Immune Feature Correlations Value Table
#'
#' @param response_tbl A tibble with columns sample, name, value
#' @param feature_tbl A tibble with columns sample, feature_name,
#' feature_display, order, feature_value, tag
#' @importFrom dplyr inner_join filter select
#' @importFrom rlang .data
#' @importFrom magrittr %>%
build_ifc_value_tbl <- function(response_tbl, feature_tbl){
    response_tbl %>%
        dplyr::select(
            "sample",
            "response_name" = "name",
            "response_value" = "value"
        ) %>%
        dplyr::inner_join(feature_tbl, by = "sample") %>%
        dplyr::filter(.data$response_name != .data$feature_name)
}

#' Build Immune Feature Correlations Heatmap Matrix
#'
#' @param tbl A tibble with columns group, feature_name, order, feature_value,
#' and repsonse value
#' @param method A string, that is the correlaiton method
#' @importFrom dplyr group_by summarise arrange select
#' @importFrom tidyr drop_na pivot_wider
#' @importFrom tibble column_to_rownames
#' @importFrom rlang .data
#' @importFrom magrittr %>%
#' @importFrom stats cor
build_ifc_heatmap_matrix <- function(tbl, method){
    tbl %>%
        dplyr::group_by(.data$group, .data$feature_display, .data$feature_order) %>%
        dplyr::summarise(cor_value = stats::cor(
            .data$feature_value,
            .data$response_value,
            method = method
        )) %>%
        dplyr::arrange(dplyr::desc(.data$feature_order)) %>%
        dplyr::select(-.data$feature_order) %>%
        tidyr::drop_na() %>%
        tidyr::pivot_wider(
            .,
            names_from = .data$group,
            values_from = .data$cor_value
        ) %>%
        tibble::column_to_rownames("feature_display") %>%
        as.matrix()
}

#' Build Immune Feature Scatterplot Tibble
#'
#' @param tbl A tibble with columns feature_display,
#' sample, tag, response_value, feature_value
#' @param .feature A string that is in the tbl feature_name column
#' @param .group A string that is in the tbl group column
#' @importFrom dplyr filter select
#' @importFrom rlang .data
#' @importFrom magrittr %>%
build_ifc_scatterplot_tbl <- function(tbl, .feature, .group){
    tbl %>%
        dplyr::filter(
            .data$feature_display == .feature,
            .data$group == .group
        ) %>%
        dplyr::select(
            "group",
            "y"     = .data$response_value,
            "x"     = .data$feature_value,
            "name"  = .data$sample
        ) %>%
        create_plotly_label(.data$name, .data$group, cols = c("x", "y"))
}
