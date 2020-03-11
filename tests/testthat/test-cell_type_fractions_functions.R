with_test_db_env({

    test_that("Build Cell Type Fractions Barplot Tibble", {
        tbl1 <- dplyr::tibble(
            sample_id = c(1:10000),
            group = "C1"
        )
        result1 <- build_ctf_barplot_tbl(
            "Immune Cell Proportion - Original", tbl1
        )
        expect_named(
            result1,
            c("x", "y", "color", "label", "error")
        )
    })
})
