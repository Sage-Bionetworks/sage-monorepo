with_test_api_env({

  ### cohort as input ---------------------------------------------------------

  cohort_obj1 <- build_cohort_object(
    filter_obj = list(
      "samples" = "PCAWG" %>%
        iatlas.app::query_dataset_samples(.) %>%
        dplyr::pull("name")
    ),
    dataset = "PCAWG",
    group_choice = "Immune_Subtype",
    group_type = "tag"
  )

  cohort_obj2 <- list(
    "dataset" = "TCGA",
    "group_type" = "tag",
    "group_name" = "Immune_Subtype"
  )

  test_that("get_cohort_feature_class_list", {
    res1 <- get_cohort_feature_class_list(cohort_obj1)
    expect_vector(res1)

  })

  ## API queries --------------------------------------------------------------
  # features ------------------------------------------------------------------

  test_that("query_feature_values_with_cohort_object", {

    result1 <- query_feature_values_with_cohort_object(
      cohort_obj2,
      "Lymphocytes_Aggregate1"
    )

    result2 <- query_feature_values_with_cohort_object(
      cohort_obj2,
      class = "Overall Proportion"
    )

    expect_named(result1, c("name", "display", "sample", "value", "order"))
    expect_named(result2, c("name", "display", "sample", "value", "order"))
    expect_length(result1$value, 9126)
    expect_length(result2$value, 25786)

    cohort_obj2 <- list(
      "dataset" = "PCAWG",
      "group_type" = "custom",
      "group_name" = "Immune_Feature_Bins"
    )
    result3 <- query_feature_values_with_cohort_object(
      cohort_obj2,
      "Lymphocytes_Aggregate1"
    )

    result4 <- query_feature_values_with_cohort_object(
      cohort_obj2,
      class = "EPIC"
    )

    expect_length(result3$value, 455)
    expect_length(result4$value, 3640)

  })

  # genes ---------------------------------------------------------------------

  test_that("query_gene_expression_with_cohort_object", {

    expected_columns <- c(
      "name",
      "entrez",
      "hgnc",
      "rna_seq_expr"
    )
    result1 <- query_gene_expression_with_cohort_object(
      cohort_obj1,
      entrez_ids = 135L
    )

    expect_named(result1, expected_columns)
    expect_equal(nrow(result1), 455L)
  })



})
