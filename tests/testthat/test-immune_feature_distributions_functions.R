cohort_obj1 <- pcawg_immune_subtype_cohort_obj

test_that("Build Distribution Plot Tibble", {

  result1 <- build_ifd_distplot_tbl(cohort_obj1, "leukocyte_fraction", "None")
  expect_named(result1, c("x", "y"))
})

