test_that("Build Distribution Plot Tibble", {
    sample_tbl <- dplyr::tibble(
        sample_id = c(1:10),
        group = c(rep("C1", 5), rep("C2", 5))
    )
    result1 <- build_distplot_tbl(sample_tbl, 1, "None")
    expect_named(result1, c("x", "y"))
})

test_that("Build Histogram Tibble", {
    distplot_tbl <- dplyr::tibble(
        y = c(1:10),
        x = c(rep("C1", 3), rep("C2", 7))
    )
    result1 <- build_histplot_tbl(distplot_tbl, "C1")
    expect_named(result1, "x")
    expect_true(nrow(result1) == 3)
    result1 <- build_histplot_tbl(distplot_tbl, "C2")
    expect_named(result1, "x")
    expect_true(nrow(result1) == 7)
})
