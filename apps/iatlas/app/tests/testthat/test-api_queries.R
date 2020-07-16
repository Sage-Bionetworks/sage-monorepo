with_test_api_env({

  # datasets ------------------------------------------------------------------

  test_that("query_datasets", {
    result <- query_datasets()
    expect_named(result, c("display", "name"))
  })

  test_that("query_dataset_samples", {
    result <- query_dataset_samples("PCAWG")
    expect_named(result, "name")
  })

  # features ------------------------------------------------------------------

  test_that("query_feature_values", {
    result1 <- query_feature_values(
      "TCGA", "Immune_Subtype", "leukocyte_fraction"
    )
    expect_named(result1, c("name", "sample", "value"))
    expect_length(result1$value, 9058)
    result1_complete <- tidyr::drop_na(result1)
    expect_length(result1_complete$value, 9058)

    result2 <- query_feature_values(
      "PCAWG", "Immune_Subtype", "Lymphocytes_Aggregate1"
    )
    expect_length(result2$value, 455)
    result2_complete <- tidyr::drop_na(result2)
    expect_length(result2_complete$value, 455)
  })

  # features_by_tag -----------------------------------------------------------

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

  # features_by_class ---------------------------------------------------------

  test_that("query_features_by_class", {
    result <- query_features_by_class()
    expect_named(
      result,
      c(
        "class",
        "display",
        "name",
        "order",
        "unit",
        "method_tag"
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

  # genes ---------------------------------------------------------------------

  test_that("query_immunomodulators", {
    result1 <- query_immunomodulators()
    expect_named(
      result1,
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
    ARG1_publications <- result1 %>%
      dplyr::filter(.data$entrez == 383L) %>%
      tidyr::unnest(cols = "publications") %>%
      dplyr::pull("pubmedId") %>%
      sort()

    expect_equal(ARG1_publications, c(19764983L, 23890059L))
  })

  # mutations -----------------------------------------------------------------

  test_that("mutations", {
    result <- query_mutations()
    expect_named(result, c("id", "entrez", "hgnc", "code"))
  })

  # related -------------------------------------------------------------------

  test_that("dataset_tags", {
    result <- query_dataset_tags("TCGA")
    expect_named(result, c("display", "name"))
    expect_equal(result$name, c("Immune_Subtype", "TCGA_Study", "TCGA_Subtype"))
  })

  # tags ----------------------------------------------------------------------

  test_that("tags", {
    result <- query_tags("TCGA", "Immune_Subtype")
    expect_named(
      result,
      c(
        "name",
        "display"
      )
    )
    expect_equal(result$name, c("C1", "C2", "C3", "C4", "C5", "C6"))
  })

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

})


