
#' Build Overall Cell Proportions Barplot Tibble
#'
#' @param value_tbl A tibble with columns feture_name, group, feature_value,
#' order
#' @importFrom magrittr %>%
#' @importFrom rlang .data
#' @importFrom dplyr group_by arrange summarise n ungroup mutate select
build_ocp_barplot_tbl <- function(value_tbl){
    value_tbl %>%
        dplyr::group_by(.data$feature_name, .data$group) %>%
        dplyr::arrange(.data$feature_order) %>%
        dplyr::summarise(
            .mean = mean(.data$feature_value),
            .count = dplyr::n()
        ) %>%
        dplyr::ungroup() %>%
        dplyr::mutate(.se = .data$.mean / sqrt(.data$.count)) %>%
        create_plotly_label(
            .data$feature_name, .data$group, c(".mean", ".se")
        ) %>%
        dplyr::select(
            x = .data$group,
            y = .data$.mean,
            color = .data$feature_name,
            .data$label,
            error = .data$.se
        )
}

#' Build Overall Cell Proportions Scatterplot Tibble
#'
#' @param value_tbl A tibble with columns sample_id, group, feature_name,
#' feature_value
#' @param group_value A string, that is in the group column of the value_tbl
#' @importFrom magrittr %>%
#' @importFrom rlang .data
#' @importFrom dplyr inner_join filter rename select
#' @importFrom tidyr drop_na pivot_wider
build_ocp_scatterplot_tbl <- function(value_tbl, group_value){
    value_tbl %>%
        dplyr::filter(
            .data$feature_display %in% c(
                "Leukocyte Fraction", "Stromal Fraction"
            ),
            .data$group == group_value
        ) %>%
        dplyr::select(
            "sample",
            "group",
            "feature_display",
            "feature_value"
        ) %>%
        tidyr::pivot_wider(
            .,
            values_from = .data$feature_value,
            names_from = .data$feature_display
        ) %>%
        tidyr::drop_na() %>%
        dplyr::rename(
            x = .data$`Stromal Fraction`,
            y = .data$`Leukocyte Fraction`
        ) %>%
        create_plotly_label(.data$sample, .data$group, c("x", "y")) %>%
        dplyr::select(.data$x, .data$y, .data$label)
}
