build_ctf_value_tbl <- function(cohort_obj, class){
    cohort_obj %>%
        query_feature_values_with_cohort_object(class = class) %>%
        dplyr::inner_join(cohort_obj$sample_tbl, by = "sample") %>%
        dplyr::select(
            "sample",
            "group",
            "feature_name" = "display",
            "feature_value" = "value",
            "feature_order" = "order"
        )
}

#' Build Cell Type Fractions Barplot Tibble
#'
#' @param class_name A string, that is the name column of the classes table
#' @param sample_tbl A tibble with columns sample_id, and group
#' @importFrom magrittr %>%
#' @importFrom rlang .data
#' @importFrom dplyr inner_join select group_by arrange summarise ungroup mutate
build_ctf_barplot_tbl <- function(tbl){
    tbl %>%
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
