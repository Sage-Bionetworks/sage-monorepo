with_test_api_env({

    cohort_obj1 <- build_cohort_object(
        filter_obj = list(
            "samples" = "PCAWG" %>%
                query_dataset_samples(.) %>%
                dplyr::pull("name")
        ),
        dataset = "PCAWG",
        group_choice = "Immune_Subtype",
        group_type = "tag"
    )

    value_tbl1 <- build_ctf_value_tbl(
        cohort_obj1,
        "Immune Cell Proportion - Multipotent Progenitor Cell Derivative Class"
    )

    value_tbl2 <- build_ctf_value_tbl(
        cohort_obj1,
        "Immune Cell Proportion - Original"
    )

    test_that("build_ctf_value_tbl", {
        expected_columns <- c(
            "sample",
            "group",
            "feature_display",
            "feature_value",
            "feature_order"
        )
        expect_named(value_tbl1, expected_columns)
        expect_named(value_tbl2, expected_columns)
    })

    test_that("Build Cell Type Fractions Barplot Tibble", {
        expected_columns <- c("x", "y", "color", "label", "error")
        barplot_tbl1 <- build_ctf_barplot_tbl(value_tbl1)
        barplot_tbl2 <- build_ctf_barplot_tbl(value_tbl2)
        expect_named(barplot_tbl1, expected_columns)
        expect_named(barplot_tbl2, expected_columns)
    })
})
