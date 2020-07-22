with_test_api_env({

    cohort_obj1 <- list(
        "dataset" = "PCAWG",
        "group_type" = "tag",
        "group_name" = "Immune_Subtype",
        "sample_tbl" = query_cohort_selector("PCAWG", "Immune_Subtype") %>%
            dplyr::select("sample", "group" = "name")
    )

    cohort_obj2 <- list(
        "dataset" = "TCGA",
        "group_type" = "custom",
        "group_name" = "Immune_Feature_Bins",
        "sample_tbl" = query_cohort_selector("TCGA", "Immune_Subtype") %>%
            dplyr::select("sample", "group" = "name")
    )

    value_tbl1 <- build_ctf_value_tbl(
        cohort_obj1,
        "Immune Cell Proportion - Multipotent Progenitor Cell Derivative Class"
    )
    value_tbl2 <- build_ctf_value_tbl(
        cohort_obj2,
        "Immune Cell Proportion - Multipotent Progenitor Cell Derivative Class"
    )


    test_that("build_ctf_value_tbl", {
        expected_columns <- c(
            "sample",
            "group",
            "feature_name",
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
