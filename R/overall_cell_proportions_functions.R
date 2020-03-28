validate_ocp_cohort_obj <- function(cohort_obj){
    needed_fractions <- c(
        'Leukocyte Fraction', 'Stromal Fraction', 'Tumor Fraction'
    )
    available_fractions <- cohort_obj %>%
        purrr::pluck("feature_tbl") %>%
        dplyr::filter(.data$class %in% "Overall Proportion") %>%
        dplyr::pull(.data$display)

    all(needed_fractions %in% available_fractions)
}


#' Build Overall Cell Proportions Value Tibble
#'
#' @param sample_tbl A tibble with columns sample_id and group
#' @importFrom magrittr %>%
#' @importFrom dplyr inner_join
build_ocp_value_tbl <- function(sample_tbl){
    subquery1 <- paste(
        'SELECT id AS feature_id, "display" AS feature_name, "order"',
        'FROM features WHERE display IN',
        "('Leukocyte Fraction', 'Stromal Fraction', 'Tumor Fraction')"
    )

    subquery2 <- paste(
        "SELECT sample_id, feature_id, value as feature_value",
        "FROM features_to_samples"
    )

    query <- paste(
        "SELECT a.feature_name, a.order, b.sample_id, b.feature_value FROM",
        "(", subquery1, ") a",
        "INNER JOIN",
        "(", subquery2, ") b",
        "ON a.feature_id = b.feature_id"
    )

    query %>%
        perform_query(
            "build overall_cell_proportions value table"
        ) %>%
        dplyr::inner_join(sample_tbl, by = "sample_id")
}

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
        dplyr::arrange(.data$order) %>%
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
#' @param sample_tbl A tibble with columns sample_id, sample_name
#' @param group_value A string, that is in the group column of the value_tbl
#' @importFrom magrittr %>%
#' @importFrom rlang .data
#' @importFrom dplyr inner_join filter rename select
#' @importFrom tidyr drop_na pivot_wider
build_ocp_scatterplot_tbl <- function(value_tbl, sample_tbl, group_value){
    value_tbl %>%
        dplyr::inner_join(sample_tbl, by = "sample_id") %>%
        dplyr::select(
            .data$sample_name,
            .data$group,
            feature = .data$feature_name,
            value = .data$feature_value) %>%
        dplyr::filter(
            .data$feature %in% c("Leukocyte Fraction", "Stromal Fraction"),
            .data$group == group_value
        ) %>%
        tidyr::pivot_wider(
            .,
            values_from = .data$value,
            names_from = .data$feature
        ) %>%
        tidyr::drop_na() %>%
        dplyr::rename(
            x = .data$`Stromal Fraction`,
            y = .data$`Leukocyte Fraction`
        ) %>%
        create_plotly_label(.data$sample_name, .data$group, c("x", "y")) %>%
        dplyr::select(.data$x, .data$y, .data$label)
}
