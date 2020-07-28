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
    result1 <- query_feature_values(
      "TCGA", "Immune_Subtype", "leukocyte_fraction"
    )
    expect_named(result1, c("name", "display", "sample", "value", "order"))
    expect_length(result1$value, 9058)
    result1_complete <- tidyr::drop_na(result1)
    expect_length(result1_complete$value, 9058)

    result2 <- query_feature_values(
      "PCAWG", "Immune_Subtype", "Lymphocytes_Aggregate1"
    )
    expect_length(result2$value, 455)

    result3 <- query_feature_values(
      "PCAWG", class = "EPIC"
    )
    expect_length(result3$value, 3640)

  })

  test_that("query_features_range", {
    expected_columns <- c("name", "display", "value_min", "value_max")
    result1 <- query_features_range("Lymphocytes_Aggregate1", "PCAWG")
    expect_named(result1, expected_columns)
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


