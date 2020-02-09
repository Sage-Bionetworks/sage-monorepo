test_that("Scale Tibble Value Column", {
    tbl <- dplyr::tibble(value = 1:5)
    result1 <- scale_tbl_value_column(tbl)
    expect_named(result1, "value")
    result2 <- scale_tbl_value_column(tbl, scale_method = "Log2")
    expect_named(result2, "value")
    result3 <- scale_tbl_value_column(tbl, scale_method = "Log2 + 1")
    expect_named(result3, "value")
    expect_error(
        scale_tbl_value_column(tbl, scale_method = "Not a method"),
        regexp = "Scale method does not exist",
        fixed = T
    )
})


test_that("Log Tibble Value Column", {
    tbl <- dplyr::tibble(value = 1:5)
    result <- log_tbl_value_column(tbl)
    expect_named(result, "value")
})
