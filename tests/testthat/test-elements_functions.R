with_test_db_env({
    test_that("Build Numeric Filter Tibble", {
        result1 <- build_numeric_filter_tbl(1)
        expect_named(result1, c("feature_max", "feature_min"))
    })

    test_that("Build Group Filter Tibble", {
        result1 <- build_group_filter_tbl(1)
        expect_type(result1, "integer")
        expect_false(is.null(names(result1)))
    })
})
