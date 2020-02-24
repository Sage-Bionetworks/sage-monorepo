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

build_ocp_barplot_tbl <- function(value_tbl){
    value_tbl %>%
        dplyr::group_by(.data$feature_name, .data$group) %>%
        dplyr::arrange(order) %>%
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


build_ocp_scatterplot_tbl <- function(value_tbl, group_value){
    sample_tbl <-
        "SELECT id AS sample_id, name AS sample_name FROM samples" %>%
        perform_query("Get sample table")

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
