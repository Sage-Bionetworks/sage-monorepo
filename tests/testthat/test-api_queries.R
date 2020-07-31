with_test_api_env({

  # copy number results -------------------------------------------------------

  test_that("query_copy_number_results", {
    result1 <- query_copy_number_results(
      datasets = "TCGA",
      tags = "C1",
      genes = 3627L,
      features = "AS",
      direction = "Amp"
    )
    expect_named(
      result1,
      c(
        "feature",
        "tag",
        "hgnc",
        "direction",
        "p_value",
        "log10_p_value",
        "mean_cnv",
        "mean_normal",
        "t_stat"
      )
    )

    result2 <- query_copy_number_results(
      datasets = "TCGA",
      tags = c("C1", "C2"),
      features = "AS",
      genes = 1:2
    )
    expect_named(
      result2,
      c(
        "feature",
        "tag",
        "hgnc",
        "direction",
        "p_value",
        "log10_p_value",
        "mean_cnv",
        "mean_normal",
        "t_stat"
      )
    )
    expect_equal(nrow(result2), 8)
  })

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
    expected_columns <- c(
      "sample",
      "feature_name",
      "feature_display",
      "feature_value",
      "feature_order"
    )

    result1 <- query_feature_values(
      datasets = "TCGA",
      parent_tags = "Immune_Subtype",
      features = "leukocyte_fraction"
    )
    result2 <- query_feature_values(
      datasets = "PCAWG",
      parent_tags = "Immune_Subtype",
      features = "Lymphocytes_Aggregate1"
    )
    result3 <- query_feature_values(
      datasets = "PCAWG",
      parent_tags ="Immune_Subtype",
      features = "leukocyte_fraction"
    )

    expect_named(result1, expected_columns)
    expect_named(result2, expected_columns)
    expect_named(result3, expected_columns)

    expect_length(result1$feature_value, 9058)
    expect_length(result2$feature_value, 455)
    expect_length(result3$feature_value, 0)
  })

  test_that("query_features_range", {
    expected_columns <- c("name", "display", "value_min", "value_max")
    result1 <- query_features_range("Lymphocytes_Aggregate1", "PCAWG")
    expect_named(result1, expected_columns)
  })

  # test_that("query_samples_to_features", {
  #   result <- query_samples_to_features("leukocyte_fraction")
  #   expect_named(
  #     result,
  #     c(
  #       "name",
  #       "sample",
  #       "value"
  #     )
  #   )
  # })
  #
  # test_that("query_samples_to_feature", {
  #   result <- query_samples_to_feature("leukocyte_fraction")
  #   expect_named(
  #     result,
  #     c(
  #       "sample",
  #       "value"
  #     )
  #   )
  # })

  # features_by_tag -----------------------------------------------------------

  test_that("query_feature_values_by_tag", {
    expected_columns <- c(
      "tag_name",
      "tag_display",
      "tag_color",
      "tag_characteristics",
      "sample",
      "value"
    )

    result1 <- query_feature_values_by_tag(
      "Lymphocytes_Aggregate1",
      datasets = "PCAWG",
      parent_tags = "Immune_Subtype"
    )
    result2 <- query_feature_values_by_tag(
      "not_a_feature",
      datasets = "PCAWG",
      parent_tags = "Immune_Subtype"
    )
    expect_named(result1, expected_columns)
    expect_named(result2, expected_columns)
    expect_equal(nrow(result1), 455)
    expect_equal(nrow(result2), 0)
  })

  test_that("query_features_values_by_tag", {
    expected_columns <- c(
      "tag_name",
      "tag_display",
      "tag_color",
      "tag_characteristics",
      "sample",
      "feature_name",
      "feature_display",
      "feature_value",
      "feature_order"
    )

    result1 <- query_features_values_by_tag(
      datasets = "PCAWG",
      parent_tags = "Immune_Subtype",
      feature_classes = "EPIC"
    )
    result2 <- query_features_values_by_tag(
      datasets = "PCAWG",
      parent_tags = "Immune_Subtype",
      feature_classes = "not_a_class"
    )
    expect_named(result1, expected_columns)
    expect_named(result2, expected_columns)
    expect_equal(nrow(result1), 3640)
    expect_equal(nrow(result2), 0)
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



  # genes ---------------------------------------------------------------------

  test_that("query_genes", {
    result1 <- query_genes()
    expect_named(result1, c("hgnc", "entrez"))
  })

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
    # ARG1_publications <- result1 %>%
    #   dplyr::filter(.data$entrez == 383L) %>%
    #   tidyr::unnest(cols = "publications") %>%
    #   dplyr::pull("pubmedId") %>%
    #   sort()
    #
    # expect_equal(ARG1_publications, c(19764983L, 23890059L))
  })

  test_that("query_io_targets", {
    result1 <- query_io_targets()
    expect_named(
      result1,
      c(
        "entrez",
        "hgnc",
        "description",
        "io_landscape_name" ,
        "pathway",
        "therapy_type"
      )
    )
  })

  test_that("query_expression_by_genes", {
    expected_columns <- c(
      "sample",
      "entrez",
      "hgnc",
      "rna_seq_expr"
    )
    result1 <- query_expression_by_genes(
      "entrez" = 135L, sample = "TCGA-XF-A9T8"
    )
    result2 <- query_expression_by_genes(
      "entrez" = 0L, sample = "TCGA-XF-A9T8"
    )
    expect_named(result1, expected_columns)
    expect_named(result2, expected_columns)
    expect_equal(nrow(result1), 1L)
    expect_equal(nrow(result2), 0L)
  })

  # gene types ----------------------------------------------------------------

  test_that("query_gene_types", {
    result1 <- query_gene_types()
    expect_named(
      result1,
      c("display", "name")
    )
  })

  test_that("query_genes_by_gene_type", {
    result1 <- query_genes_by_gene_type()
    expect_named(
      result1,
      c("entrez", "hgnc", "gene_type_name", "gene_type_display")
    )
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

  # samples by mutation status ------------------------------------------------

  test_that("query_samples_by_mutation_status", {
    expected_names <- c("sample", "status")
    result1 <- query_samples_by_mutation_status()
    result2 <- query_samples_by_mutation_status(777, "Mut", "TCGA-2Z-A9J8")
    result3 <- query_samples_by_mutation_status(777, "Mut", "none")
    expect_named(result1, expected_names)
    expect_named(result2, expected_names)
    expect_named(result3, expected_names)
    expect_equal(nrow(result3), 0)
  })

  # samples by tag ------------------------------------------------------------

  test_that("query_samples_by_tag", {
    expected_names <- c(
      "tag_name",
      "tag_display",
      "tag_characteristics",
      "tag_color",
      "sample"
    )
    result1 <- query_samples_by_tag(
      datasets = "PCAWG", parent_tags = "Immune_Subtype"
    )
    result2 <- query_samples_by_tag(
      datasets = "PCAWG", parent_tags = "not_a_tag"
    )
    expect_named(result1, expected_names)
    expect_named(result2, expected_names)
    expect_equal(nrow(result1), 455)
    expect_equal(nrow(result2), 0)
  })

  test_that("query_tag_samples", {
    expected_names <- c("sample")
    result1 <- query_tag_samples(datasets = "PCAWG", tags = "C1")
    result2 <- query_tag_samples(datasets = "PCAWG", tags = "C5")
    expect_named(result1, expected_names)
    expect_named(result2, expected_names)
    expect_equal(nrow(result2), 0)
  })

  # tags ----------------------------------------------------------------------

  test_that("tags", {
    expected_columns <- c(
      "name",
      "display",
      "characteristics",
      "color",
      "sample_count"
    )

    result <- query_tags("TCGA", "Immune_Subtype")
    expect_named(result, expected_columns)
    expect_equal(result$name, c("C1", "C2", "C3", "C4", "C5", "C6"))
  })

  test_that("query_cohort_selector", {
    expected_columns <- c(
      "name",
      "display",
      "characteristics",
      "color",
      "size",
      "samples"
    )

    result1 <- query_cohort_selector("PCAWG", "Immune_Subtype")
    result2 <- query_cohort_selector("PCAWG", "TCGA_Subtype")
    expect_named(result1, expected_columns)
    expect_named(result2, expected_columns)
    expect_equal(nrow(result1), 5)
    expect_equal(nrow(result2), 0)
  })

})


