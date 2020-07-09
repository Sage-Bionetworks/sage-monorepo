with_test_api_env({

  test_that("query_cohort_selector", {
    result <- query_cohort_selector()
    expect_named(
      result,
      c(
        "name",
        "display",
        "characteristics",
        "color",
        "size",
        "sample"
      )
    )
  })

  test_that("query_features_by_class", {
    result <- query_features_by_class()
    expect_named(
      result,
      c(
        "class",
        "features"
      )
    )
  })

  test_that("query_samples_to_features", {
    result <- query_samples_to_features("leukocyte_fraction")
    expect_named(
      result,
      c(
        "name",
        "sample",
        "value"
      )
    )
  })

  test_that("query_samples_to_feature", {
    result <- query_samples_to_feature("leukocyte_fraction")
    expect_named(
      result,
      c(
        "sample",
        "value"
      )
    )
  })

  test_that("query_immunomodulators", {
    result <- query_immunomodulators()
    expect_named(
      result,
      c(
        "entrez",
        "hgnc",
        "friendly_name",
        "description",
        "gene_family",
        "gene_function",
        "immune_checkpoint",
        "super_category",
        "publications"
      )
    )
  })

  test_that("query_feature_values_by_tag", {
    result <- query_feature_values_by_tag(
      "TCGA", "Immune_Subtype", "leukocyte_fraction"
    )
    expect_named(
      result,
      c(
        "characteristics",
        "display",
        "features",
        "tag"
      )
    )
    expect_named(result$features[[1]], c("sample", "value"))
  })

  test_that("query_features_values_by_tag", {
    result <- query_features_values_by_tag(
      "TCGA", "Immune_Subtype", feature_class = "DNA Alteration"
    )
    expect_named(
      result,
      c(
        "tag",
        "sample",
        "feature_name",
        "feature_display",
        "feature_order",
        "feature_value"
      )
    )
  })

  test_that("query_feature_values", {
    result <- query_feature_values(
      "TCGA", "Immune_Subtype", "leukocyte_fraction"
    )
    expect_named(
      result,
      c(
        "name",
        "sample",
        "value"
      )
    )
  })

  test_that("query_dataset_samples", {
    result <- query_dataset_samples("PCAWG")
    expect_named(result, "name")
  })
})


