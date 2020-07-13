with_test_api_env({
    # test_that("Build Numeric Filter Tibble", {
    #     result1 <- build_numeric_filter_tbl(1)
    #     expect_named(result1, c("feature_max", "feature_min"))
    # })

    test_that("Build Tag Filter Named List", {
        result1 <- build_tag_filter_named_list("Immune_Subtype")
        expect_equal(result1, c("C1", "C2", "C3", "C4", "C5", "C6"))
    })
})
