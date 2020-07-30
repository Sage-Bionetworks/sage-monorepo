with_test_api_env({

    cohort_obj1 <- build_cohort_object(
        filter_obj = list(
            "samples" = "TCGA" %>%
                iatlas.app::query_dataset_samples(.) %>%
                dplyr::pull("name")
        ),
        dataset = "TCGA",
        group_choice = "Immune_Subtype",
        group_type = "tag"
    )

    value_tbl1 <- build_ocp_value_tbl(cohort_obj1)

    test_that("build_ocp_value_tbl", {
        expected_columns <- c(
            "sample",
            "group",
            "feature_display",
            "feature_value",
            "feature_order"
        )
        expect_named(value_tbl1, expected_columns)
    })

    test_that("Build Overall Cell Proportions Barplot Tibble", {
        expected_columns <- c("x", "y", "color", "label", "error")
        barplot_tbl1 <- build_ocp_barplot_tbl(value_tbl1)
        expect_named(barplot_tbl1, expected_columns)
    })

    test_that("Build Overall Cell Proportions Scatterplot Tibble", {
        expected_columns <- c("x", "y", "label")
        scatterplot_tbl1 <- build_ocp_scatterplot_tbl(value_tbl1, "C1")
        expect_named(scatterplot_tbl1, expected_columns)
    })

})

