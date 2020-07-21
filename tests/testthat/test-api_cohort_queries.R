with_test_api_env({

  # features ------------------------------------------------------------------

  test_that("query_feature_values_with_cohort_object", {

    cohort_obj1 <- list(
      "dataset" = "TCGA",
      "group_type" = "tag",
      "group_name" = "Immune_Subtype"
    )

    result1 <- query_feature_values_with_cohort_object(
      cohort_obj1,
      "Lymphocytes_Aggregate1"
    )

    result2 <- query_feature_values_with_cohort_object(
      cohort_obj1,
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



})
