build_tm_dt_tbl <- function(sample_tbl){
    subquery1 <- "SELECT id FROM classes WHERE name = 'TIL Map Characteristic'"

    subquery2 <- paste(
        "SELECT id AS feature FROM features",
        "WHERE class_id = (",
        subquery1,
        ")"
    )

    subquery3 <- paste(
        "SELECT feature_id, sample_id, value FROM features_to_samples",
        "WHERE feature_id IN (",
        subquery2,
        ")"
    )

    query <- paste(
        "SELECT a.sample_id, a.value, b.display FROM",
        "(", subquery3, ") a",
        "INNER JOIN",
        "(SELECT id, display from features) b",
        "ON a.feature_id = b.id"
    )

    query %>%
        perform_query("build feature table") %>%
        dplyr::mutate(value = round(.data$value, digits = 1)) %>%
        tidyr::pivot_wider(
            .,
            names_from = .data$display,
            values_from = .data$value
        ) %>%
        dplyr::inner_join(sample_tbl, by = "sample_id") %>%
        dplyr::filter(!is.na(.data$slide_barcode)) %>%
        dplyr::mutate(Image = paste0(
            "<a href=\"",
            "https://quip1.bmi.stonybrook.edu:443/",
            "camicroscope/osdCamicroscope.php?tissueId=",
            .data$slide_barcode,
            "\">",
            .data$slide_barcode,
            "</a>"
        )) %>%
        dplyr::select(-c(.data$slide_barcode, .data$sample_id)) %>%
        dplyr::select(
            Sample = .data$sample_name,
            `Selected Group` = .data$group,
            .data$Image,
            dplyr::everything()
        )
}
