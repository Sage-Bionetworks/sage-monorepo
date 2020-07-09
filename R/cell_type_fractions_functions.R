#' Build Cell Type Fractions Barplot Tibble
#'
#' @param class_name A string, that is the name column of the classes table
#' @param sample_tbl A tibble with columns sample_id, and group
#' @importFrom magrittr %>%
#' @importFrom rlang .data
#' @importFrom dplyr inner_join select group_by arrange summarise ungroup mutate
build_ctf_barplot_tbl <- function(data_tbl){
    data_tbl %>%
        print() %>%
        dplyr::rename("group" = "tag") %>%
        dplyr::group_by(.data$feature_display, .data$group) %>%
        dplyr::arrange(.data$feature_order) %>%
        dplyr::summarise(
            .mean = mean(.data$feature_value),
            .count = dplyr::n()
        ) %>%
        dplyr::ungroup() %>%
        dplyr::mutate(.se = .data$.mean / sqrt(.data$.count)) %>%
        create_plotly_label(
            .data$feature_display, .data$group, c(".mean", ".se")
        ) %>%
        dplyr::select(
            x = .data$group,
            y = .data$.mean,
            color = .data$feature_display,
            .data$label,
            error = .data$.se
        )
}
