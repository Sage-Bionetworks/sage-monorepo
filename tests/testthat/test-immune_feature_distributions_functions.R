with_test_api_env({
  test_that("Build Distribution Plot Tibble", {
    sample_tbl <- iatlas.app::query_cohort_selector() %>%
      dplyr::select("group" = "name", "sample_name" = "samples") %>%
      tidyr::unnest(cols = c(sample_name))

    result1 <- build_ifd_distplot_tbl(sample_tbl, "leukocyte_fraction", "None")
    expect_named(result1, c("x", "y"))
  })
})
