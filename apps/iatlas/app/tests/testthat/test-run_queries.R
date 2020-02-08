test_that("build_cohort_tbl_by_group", {
    expect_named(
        build_cohort_tbl_by_group(c(1:10), "Immune Subtype"),
        c("sample_id", "group", "name", "characteristics", "color")
    )
})

test_that("build_cohort_tbl_by_feature_id", {
    expect_named(
        build_cohort_tbl_by_feature_id(c(1:10), 1),
        c("sample_id", "value")
    )
})





