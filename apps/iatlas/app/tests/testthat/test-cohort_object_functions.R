### cohort as input -----------------------------------------------------------
## extract from cohort --------------------------------------------------------

test_that("get_cohort_feature_class_list", {
  res1 <- get_cohort_feature_class_list(pcawg_immune_subtype_cohort_obj)
  expect_vector(res1)
})

## check cohort for module display --------------------------------------------

test_that("cohort_has_features", {
  expect_true(cohort_has_features(
    tcga_immune_subtype_cohort_obj_50,
    c("leukocyte_fraction", "Stromal_Fraction", "Tumor_fraction")
  ))
  expect_false(cohort_has_features(
    pcawg_immune_subtype_cohort_obj,
    c("leukocyte_fraction", "Stromal_Fraction", "Tumor_fraction")
  ))
  expect_true(cohort_has_features(
    tcga_immune_subtype_cohort_obj_50,
    c("leukocyte_fraction", "Stromal_Fraction", "not_a_feature"),
    all_features = F
  ))
  expect_false(cohort_has_features(
    tcga_immune_subtype_cohort_obj,
    c("not_a_feature"),
    all_features = F
  ))
})

test_that("cohort_has_classes", {
  expect_true(cohort_has_classes(
    tcga_immune_subtype_cohort_obj_50,
    c("DNA Alteration", "TIL Map Characteristic")
  ))
  expect_false(cohort_has_classes(
    pcawg_immune_subtype_cohort_obj,
    c("DNA Alteration", "TIL Map Characteristic")
  ))
  expect_true(cohort_has_classes(
    tcga_immune_subtype_cohort_obj_50,
    c("DNA Alteration", "not_a_class"),
    all_classes = F
  ))
  expect_false(cohort_has_classes(
    tcga_immune_subtype_cohort_obj,
    c("not_a_class"),
    all_classes = F
  ))
})

test_that("show_co_submodules", {
  expect_true(show_co_submodules(tcga_immune_subtype_cohort_obj_50))
  expect_true(show_co_submodules(tcga_feature_bin_cohort_obj_50))
  expect_false(show_co_submodules(pcawg_immune_subtype_cohort_obj))
})

test_that("show_ud_submodule", {
  expect_true(show_ud_submodule(tcga_immune_subtype_cohort_obj_50))
  expect_false(show_ud_submodule(pcawg_immune_subtype_cohort_obj))
  expect_false(show_ud_submodule(tcga_feature_bin_cohort_obj_50))
})

test_that("show_md_submodule", {
  expect_true(show_md_submodule(tcga_immune_subtype_cohort_obj_50))
  expect_false(show_md_submodule(pcawg_immune_subtype_cohort_obj))
  expect_true(show_md_submodule(tcga_feature_bin_cohort_obj_50))
})

test_that("show_tilmap_submodules", {
  expect_true(show_tilmap_submodules(tcga_immune_subtype_cohort_obj_50))
  expect_false(show_tilmap_submodules(pcawg_immune_subtype_cohort_obj))
  expect_true(show_tilmap_submodules(tcga_feature_bin_cohort_obj_50))
})

test_that("show_ocp_submodule", {
  expect_true(show_ocp_submodule(tcga_immune_subtype_cohort_obj_50))
  expect_false(show_ocp_submodule(pcawg_immune_subtype_cohort_obj))
  expect_true(show_ocp_submodule(tcga_feature_bin_cohort_obj_50))
})

test_that("show_ctf_submodule", {
  expect_true(show_ctf_submodule(tcga_immune_subtype_cohort_obj_50))
  expect_true(show_ctf_submodule(pcawg_immune_subtype_cohort_obj))
  expect_true(show_ctf_submodule(tcga_feature_bin_cohort_obj_50))
})

## API queries --------------------------------------------------------------
# features ------------------------------------------------------------------

test_that("query_feature_values_with_cohort_object", {

  expected_columns <- c(
    "sample",
    "feature_name",
    "feature_display",
    "feature_value",
    "feature_order",
    "feature_class"
  )

  result1 <- query_feature_values_with_cohort_object(
    pcawg_immune_subtype_cohort_obj,
    feature = "Lymphocytes_Aggregate1"
  )
  expect_named(result1, expected_columns)
  expect_length(result1$feature_value, 455L)

  result2 <- query_feature_values_with_cohort_object(
    pcawg_immune_subtype_cohort_obj,
    class = "Overall Proportion"
  )
  expect_named(result2, expected_columns)
  expect_length(result2$feature_value, 0)

  result3 <- query_feature_values_with_cohort_object(
    pcawg_immune_subtype_cohort_obj,
    class = "EPIC"
  )
  expect_named(result3, expected_columns)
  expect_length(result3$feature_value, 3640L)

  result4 <- query_feature_values_with_cohort_object(
    pcawg_feature_bin_cohort_obj,
    class = "EPIC"
  )

  result5 <- query_feature_values_with_cohort_object(
    tcga_immune_subtype_cohort_obj_50,
    feature = "OS_time"
  )

  result6 <- query_feature_values_with_cohort_object(
    list(
      "dataset" = "PCAWG",
      "group_name" = "COAD",
      "group_type" = "User Defined Group",
      "sample_tbl" = iatlas.api.client::query_dataset_samples(datasets = "PCAWG") %>%
        dplyr::rename("sample" = "name")
    ),
    feature = "Lymphocytes_Aggregate1"
  )
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
    pcawg_immune_subtype_cohort_obj,
    entrez = 135L
  )

  expect_named(result1, expected_columns)
  expect_equal(nrow(result1), 455L)
})
