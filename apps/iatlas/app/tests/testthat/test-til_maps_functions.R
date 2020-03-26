with_test_db_env({
    test_that("Build Tilmap Sample Tibble", {
        tbl1 <- dplyr::tibble(
            sample_id = 1:10000,
            group = "G1"
        )
        result1 <- build_tm_sample_tbl(tbl1)
        expect_named(
            result1,
            c("sample_id", "sample_name", "slide_barcode", "group")
        )
    })

})
