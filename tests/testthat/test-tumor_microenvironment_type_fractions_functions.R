
value_tbl1 <- build_ctf_value_tbl(
  pcawg_immune_subtype_cohort_obj,
  "Immune Cell Proportion - Multipotent Progenitor Cell Derivative Class"
)

value_tbl2 <- build_ctf_value_tbl(
  pcawg_immune_subtype_cohort_obj,
  "Immune Cell Proportion - Original"
)

test_that("build_ctf_value_tbl", {
  expected_columns <- c(
    "sample",
    "group",
    "feature_display",
    "feature_value",
    "feature_order"
  )
  expect_named(value_tbl1, expected_columns)
  expect_named(value_tbl2, expected_columns)
})

test_that("Build Cell Type Fractions Barplot Tibble", {
  expected_columns <- c("x", "y", "color", "label", "error")
  barplot_tbl1 <- build_ctf_barplot_tbl(value_tbl1)
  barplot_tbl2 <- build_ctf_barplot_tbl(value_tbl2)
  expect_named(barplot_tbl1, expected_columns)
  expect_named(barplot_tbl2, expected_columns)
})

