with_test_api_env({

    sample_tbl <- query_cohort_selector(
        "TCGA",
        "Immune_Subtype"
    ) %>%
        dplyr::select("sample", "group" = "name")

    value_tbl <-
        query_features_values_by_tag(
            "TCGA",
            "Immune_Subtype",
            list("leukocyte_fraction", "Stromal_Fraction", "Tumor_fraction")
        ) %>%
        dplyr::rename("group" = "tag") %>%
        dplyr::filter(sample %in% sample_tbl$sample)

    test_that("Build Overall Cell Proportions Barplot Tibble", {
        barplot_tbl <- build_ocp_barplot_tbl(value_tbl)
        expect_named(
            barplot_tbl,
            c("x", "y", "color", "label", "error")
        )
    })

    test_that("Build Overall Cell Proportions Scatterplot Tibble", {
        barplot_tbl <- build_ocp_scatterplot_tbl(value_tbl, "C1")
        expect_named(
            barplot_tbl,
            c("x", "y", "label")
        )
    })

})

