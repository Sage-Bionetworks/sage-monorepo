build_ctf_barplot_tbl <- function(class_name, sample_tbl){
    subquery1 <- paste0(
        "SELECT id FROM classes WHERE name = '",
        class_name,
        "'"
    )

    subquery2 <- paste(
        'SELECT id AS feature_id, name as feature_name, "order" FROM',
        'features WHERE class_id = (',
        subquery1,
        ')'
    )

    subquery3 <- paste(
        "SELECT sample_id, feature_id, value as feature_value",
        "FROM features_to_samples"
    )

    query <- paste(
        "SELECT a.sample_id, a.feature_id, a.feature_value,",
        "b.feature_name, b.order FROM (", subquery3, ") a",
        "INNER JOIN (", subquery2, ") b",
        "ON a.feature_id = b.feature_id"
    )

    query %>%
        perform_query("build feature table") %>%
        dplyr::inner_join(sample_tbl, by = "sample_id") %>%
        dplyr::select(-.data$sample_id) %>%
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
