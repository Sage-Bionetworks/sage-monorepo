#' Build Immune Feature Correlations Value Table
#'
#' @param response_tbl A tibble with columns sample_id, feature_id, and value
#' @param feature_tbl A tibble with columns sample_id, feature_id, value, and
#' order
#' @param sample_tbl A tibble with columns sample_id, and group
#' @importFrom purrr reduce
#' @importFrom dplyr inner_join filter select
#' @importFrom rlang .data
#' @importFrom magrittr %>%
build_ifc_value_tbl <- function(response_tbl, feature_tbl, sample_tbl){
    tbl <-
        purrr::reduce(
            list(response_tbl, feature_tbl, sample_tbl),
            dplyr::inner_join,
            by = "sample_id"
        ) %>%
        dplyr::filter(.data$feature_id.x != .data$feature_id.y) %>%
        dplyr::select(
            response_value = .data$value.x,
            feature_value  = .data$value.y,
            feature_name   = .data$feature,
            .data$sample_id,
            .data$order,
            .data$group
        )
    return(tbl)
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
build_ifc_heatmap_matrix <- function(tbl, method){
    tbl %>%
        dplyr::group_by(.data$group, .data$feature_name, .data$order) %>%
        dplyr::summarise(cor_value = cor(
            .data$feature_value,
            .data$response_value,
            method = method
        )) %>%
        dplyr::arrange(dplyr::desc(.data$order)) %>%
        dplyr::select(-.data$order) %>%
        tidyr::drop_na() %>%
        tidyr::pivot_wider(
            .,
            names_from = .data$group,
            values_from = .data$cor_value
        ) %>%
        tibble::column_to_rownames("feature_name") %>%
        as.matrix()
}

build_ifc_scatterplot_tbl <- function(tbl, clicked_feature, clicked_group){
    sample_tbl <-
        "SELECT id AS sample_id, name AS sample_name FROM samples" %>%
        .GlobalEnv$perform_query("Get sample table")

    tbl %>%
        dplyr::filter(
            .data$feature_name == clicked_feature,
            .data$group == clicked_group
        ) %>%
        dplyr::inner_join(sample_tbl, by = "sample_id") %>%
        dplyr::select(
            .data$group,
            y    = .data$response_value,
            x    = .data$feature_value,
            name = .data$sample_name
        ) %>%
        create_plotly_label(.data$name, .data$group, cols = c("x", "y"))
}
