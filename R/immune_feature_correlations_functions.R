build_ifc_response_tbl <- function(cohort_obj, response){
    cohort_obj %>%
        query_feature_values_with_cohort_object(response) %>%
        dplyr::select(
            "sample",
            "response_name" = "display",
            "response_value" = "value"
        )
}

build_ifc_feature_tbl <- function(cohort_obj, feature_class){
    cohort_obj %>%
        query_feature_values_with_cohort_object(class = feature_class) %>%
        dplyr::select(
            "sample",
            "feature_name" = "display",
            "feature_value" = "value",
            "feature_order" = "order"
        )
}



#' Build Immune Feature Correlations Value Table
#'
#' @param response_tbl A tibble with columns sample, name, value
#' @param feature_tbl A tibble with columns sample, feature_name,
#' feature_display, order, feature_value, tag
#' @importFrom dplyr inner_join filter select
#' @importFrom rlang .data
#' @importFrom magrittr %>%
build_ifc_value_tbl <- function(response_tbl, feature_tbl, sample_tbl){
    response_tbl %>%
        dplyr::inner_join(feature_tbl, by = "sample") %>%
        dplyr::filter(.data$feature_name != .data$response_name) %>%
        dplyr::inner_join(sample_tbl, by = "sample") %>%
        dplyr::select(
            "sample",
            "group",
            "response_value",
            "feature_value",
            "feature_name",
            "feature_order"
        )
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
        dplyr::group_by(.data$group, .data$feature_name, .data$feature_order) %>%
        dplyr::summarise("cor_value" = stats::cor(
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
        tibble::column_to_rownames("feature_name") %>%
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
            .data$feature_name == .feature,
            .data$group == .group
        ) %>%
        dplyr::select(
            "group",
            "y"     = .data$response_value,
            "x"     = .data$feature_value,
            "sample"
        ) %>%
        create_plotly_label(.data$sample, .data$group, cols = c("x", "y")) %>%
        dplyr::select("label", "x", "y")
}
