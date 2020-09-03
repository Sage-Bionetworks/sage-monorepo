
cohort_obj1 <- build_cohort_object(
  filter_obj = list(
    "samples" = "PCAWG" %>%
      iatlas.api.client::query_dataset_samples(.) %>%
      dplyr::pull("name")
  ),
  dataset = "PCAWG",
  group_choice = "Immune_Subtype",
  group_type = "tag"
)

cohort_obj2 <- build_cohort_object(
  filter_obj = list(
    "samples" = "PCAWG" %>%
      iatlas.api.client::query_dataset_samples(.) %>%
      dplyr::pull("name")
  ),
  dataset = "PCAWG",
  group_choice = "Immune Feature Bins",
  group_type = "custom",
  feature_name = "EPIC_B_Cells",
  bin_number = 2,
  feature_tbl = "PCAWG" %>%
    iatlas.api.client::query_features_by_class() %>%
    dplyr::select("class", "display", "name")
)

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
