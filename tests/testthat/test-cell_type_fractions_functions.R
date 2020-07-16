with_test_api_env({

    sample_tbl <- iatlas.app::query_cohort_selector(
        "PCAWG",
        "Immune_Subtype"
    ) %>%
        dplyr::select("sample", "group" = "name")

    value_tbl <- iatlas.app::query_features_values_by_tag(
        "PCAWG",
        "Immune_Subtype",
        feature_class = "Immune Cell Proportion - Multipotent Progenitor Cell Derivative Class"
    ) %>%
        dplyr::rename("group" = "tag") %>%
        dplyr::filter(.data$sample %in% sample_tbl$sample)

    plot_tbl <- iatlas.app::build_ctf_barplot_tbl(value_tbl)

    test_that("Build Cell Type Fractions Barplot Tibble", {

        expect_named(
            plot_tbl,
            c("x", "y", "color", "label", "error")
        )
    })
})
