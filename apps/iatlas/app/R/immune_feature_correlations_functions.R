build_ifc_response_tbl <- function(cohort_obj, response){
    cohort_obj %>%
        query_feature_values_with_cohort_object(response) %>%
        dplyr::select(
            "sample",
            "response_display" = "feature_display",
            "response_value" = "feature_value"
        )
}

build_ifc_feature_tbl <- function(cohort_obj, feature_class){
    cohort_obj %>%
        query_feature_values_with_cohort_object(class = feature_class) %>%
        dplyr::select(
            "sample",
            "feature_display",
            "feature_value",
            "feature_order"
        )
}

build_ifc_value_tbl <- function(response_tbl, feature_tbl, sample_tbl){
    response_tbl %>%
        dplyr::inner_join(feature_tbl, by = "sample") %>%
        dplyr::filter(.data$feature_display != .data$response_display) %>%
        dplyr::inner_join(sample_tbl, by = "sample") %>%
        dplyr::select(
            "sample",
            "group",
            "response_value",
            "feature_value",
            "feature_display",
            "feature_order"
        )
}

build_ifc_heatmap_matrix <- function(tbl, method){
    tbl %>%
        dplyr::group_by(.data$group, .data$feature_display, .data$feature_order) %>%
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
        tibble::column_to_rownames("feature_display") %>%
        as.matrix()
}

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
            "sample"
        ) %>%
        iatlas.modules::create_plotly_text(
            .data$sample,
            .data$group,
            cols = c("x", "y"),
            title = "ParticipantBarcode"
        ) %>%
        dplyr::select("label" = "text", "x", "y")
}
