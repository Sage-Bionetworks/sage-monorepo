
cohort_obj1 <- tcga_immune_subtype_cohort_obj_50

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
