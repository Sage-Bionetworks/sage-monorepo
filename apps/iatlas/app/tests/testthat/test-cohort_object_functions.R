
cohort_obj1 <- pcawg_immune_subtype_cohort_obj

cohort_obj2 <- pcawg_feature_bin_cohort_obj

test_that("get_cohort_feature_class_list", {
  res1 <- get_cohort_feature_class_list(cohort_obj1)
  expect_vector(res1)
})


## API queries --------------------------------------------------------------
# features ------------------------------------------------------------------

test_that("query_feature_values_with_cohort_object", {

  expected_columns <- c(
    "sample",
    "feature_name",
    "feature_display",
    "feature_value",
    "feature_order"
  )

  result1 <- query_feature_values_with_cohort_object(
    cohort_obj1,
    feature = "Lymphocytes_Aggregate1"
  )

  result2 <- query_feature_values_with_cohort_object(
    cohort_obj1,
    class = "Overall Proportion"
  )

  result3 <- query_feature_values_with_cohort_object(
    cohort_obj1,
    class = "EPIC"
  )

  result4 <- query_feature_values_with_cohort_object(
    cohort_obj2,
    class = "EPIC"
  )

  expect_named(result1, expected_columns)
  expect_named(result2, expected_columns)
  expect_named(result3, expected_columns)
  expect_length(result1$feature_value, 455L)
  expect_length(result2$feature_value, 0)
  expect_length(result3$feature_value, 3640L)

})

# genes ---------------------------------------------------------------------

test_that("query_gene_expression_with_cohort_object", {

  expected_columns <- c(
    "sample",
    "entrez",
    "hgnc",
    "rna_seq_expr"
  )
  result1 <- query_gene_expression_with_cohort_object(
    cohort_obj1,
    entrez = 135L
  )

  expect_named(result1, expected_columns)
  expect_equal(nrow(result1), 455L)
})
