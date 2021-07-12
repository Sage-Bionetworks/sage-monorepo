
cohort_obj1 <- get_tcga_immune_subtype_cohort_obj_50()

cov_obj1 <- list(
  "categorical_covariates" = NULL, "numerical_covariates" = NULL
)
cov_obj2 <- list(
  "categorical_covariates" = c("Immune_Subtype", "TCGA_Study"),
  "numerical_covariates" = NULL
)
cov_obj3 <- list(
  "categorical_covariates" = NULL,
  "numerical_covariates" = c(
    "leukocyte_fraction", "Lymphocytes_Aggregate1"
  )
)
cov_obj4 <- list(
  "categorical_covariates" = c("Immune_Subtype", "TCGA_Study"),
  "numerical_covariates" = c(
    "leukocyte_fraction", "Lymphocytes_Aggregate1"
  )
)

tag_cov_tbl1 <- build_md_tag_covariate_tbl(cohort_obj1, cov_obj1)
tag_cov_tbl2 <- build_md_tag_covariate_tbl(cohort_obj1, cov_obj2)

test_that("build_md_tag_covariate_tbl", {
  expect_null(tag_cov_tbl1)
  expect_named(tag_cov_tbl2, c("sample", "Immune_Subtype", "TCGA_Study"))
})

feature_cov_tbl1 <- build_md_feature_covariate_tbl(cohort_obj1, cov_obj1)
feature_cov_tbl2 <- build_md_feature_covariate_tbl(cohort_obj1, cov_obj3)

test_that("build_md_feature_covariate_tbl", {
  expect_null(feature_cov_tbl1)
  expect_named(
    feature_cov_tbl2,
    c("sample", "leukocyte_fraction", "Lymphocytes_Aggregate1")
  )
})

cov_tbl1 <- build_md_covariate_tbl(cohort_obj1, cov_obj1)
cov_tbl2 <- build_md_covariate_tbl(cohort_obj1, cov_obj2)
cov_tbl3 <- build_md_covariate_tbl(cohort_obj1, cov_obj3)
cov_tbl4 <- build_md_covariate_tbl(cohort_obj1, cov_obj4)

test_that("Build Multivariate Driver Covariate Tibble", {

  expect_null(cov_tbl1)
  expect_named(cov_tbl2, c("sample", "Immune_Subtype", "TCGA_Study"))
  expect_named(
    cov_tbl3,
    c("sample", "leukocyte_fraction", "Lymphocytes_Aggregate1")
  )
  expect_named(
    cov_tbl4,
    c(
      "sample",
      "Immune_Subtype",
      "TCGA_Study",
      "leukocyte_fraction",
      "Lymphocytes_Aggregate1"
    )
  )
})

response_tbl1 <- build_md_response_tbl(cohort_obj1, "leukocyte_fraction")

test_that("Build Multivariate Driver Response Tibble", {
  expect_named(response_tbl1, c("sample", "response")
  )
})

status_tbl1 <- iatlas.api.client::query_mutation_statuses(
  samples = cohort_obj1$sample_tbl$sample,
  entrez = 1,
) %>%
  dplyr::select(
    "mutation" = "mutation_name",
    "sample" = "sample_name",
    "status" = "mutation_status"
  )

test_that("Combine Multivariate Driver Tibbles", {
  result1 <- combine_md_tbls(
    response_tbl1,
    status_tbl1,
    cohort_obj1$sample_tbl,
    NULL,
    "By group"
  )
  result2 <- combine_md_tbls(
    response_tbl1,
    status_tbl1,
    cohort_obj1$sample_tbl,
    NULL,
    "Across groups"
  )
  result3 <- combine_md_tbls(
    response_tbl1,
    status_tbl1,
    cohort_obj1$sample_tbl,
    feature_cov_tbl2,
    "By group"
  )
  result4 <- combine_md_tbls(
    response_tbl1,
    status_tbl1,
    cohort_obj1$sample_tbl,
    feature_cov_tbl2,
    "Across groups"
  )

  expect_named(result1, c("response", "status", "label"))
  expect_named(result2, c("response", "status", "label"))
  expect_named(
    result3,
    c("response", "status", "leukocyte_fraction", "Lymphocytes_Aggregate1", "label")
  )
  expect_named(
    result4,
    c("response", "status", "leukocyte_fraction", "Lymphocytes_Aggregate1", "label")
  )
})

test_that("Filter Multivariate Driver Labels", {
    tbl <- dplyr::tribble(
        ~label, ~status,
        "l1",   "Wt",
        "l1",   "Mut",
        "l2",   "Wt",
        "l2",   "Wt",
        "l2",   "Wt",
    )
    expect_equal(filter_md_labels(tbl, 0, 0), c("l1", "l2"))
    expect_equal(filter_md_labels(tbl, 1, 1), "l1")
    expect_equal(filter_md_labels(tbl, 2, 2), character(0))
})

test_that("Build Multivariate Driver Pvalue Tibble", {
    tbl <- dplyr::tribble(
        ~label, ~response, ~status,
        "l1",   1,         "Wt",
        "l1",   .9,        "Wt",
        "l1",   .8,        "Mut",
        "l1",   .7,        "Mut",
    )
    result1 <- build_md_pvalue_tbl(tbl, "response ~ status")
    expect_named(result1,  c("label", "p_value", "log10_p_value")
    )
})

test_that("Calculate Linear Model Pvalue", {
    tbl <- dplyr::tribble(
        ~label, ~response, ~status,
        "l1",   1,         "Wt",
        "l1",   .9,        "Wt",
        "l1",   .8,        "Mut",
        "l1",   .7,        "Mut",
    )
    result1 <- calculate_lm_pvalue(tbl, "response ~ status", "statusWt")
    expect_type(result1, "double")
})

test_that("Build Multivariate Driver Effect Size Tibble", {
    tbl <- dplyr::tribble(
        ~label, ~response, ~status,
        "l1",   1,         "Wt",
        "l1",   .9,        "Wt",
        "l1",   .8,        "Mut",
        "l1",   .7,        "Mut",
    )
    result1 <- build_md_effect_size_tbl(tbl)
    expect_named(result1,  c("label", "fold_change", "log10_fold_change"))
})

test_that("Get Effect Size From Tibble", {
    tbl <- dplyr::tibble(g1 = list(2:4), g2 = list(1:11))
    expect_equal(get_effect_size_from_tbl(tbl), 1/2)
})

test_that("Calculate Ratio Effect Size", {
    expect_equal(calculate_ratio_effect_size(2:4, 1:11), 1/2)
    expect_true(is.na(calculate_ratio_effect_size(-3:1, 1:11)))
    expect_true(is.na(calculate_ratio_effect_size(1:3, 0)))
})

test_that("Build Multivariate Driver Violin Tibble", {
    tbl <- dplyr::tribble(
        ~label, ~response, ~status,
        "l1",   1,         "Wt",
        "l1",   .9,        "Wt",
        "l1",   .8,        "Mut",
        "l1",   .7,        "Mut",
        "l2",   100,       "Wt",
        "l2",   110,       "Mut",
        "l2",   90,        "Mut",
        "l2",   70,        "Mut",
        "l2",   60,        "Mut",
    )
    result1 <- build_md_driver_violin_tbl(tbl, "l1")
    result2 <- build_md_driver_violin_tbl(tbl, "l2")
    expect_named(result1, c("x", "y"))
    expect_named(result2, c("x", "y"))
    expect_equal(nrow(result1), 4L)
    expect_equal(nrow(result2), 5L)
})

test_that("Create Multivariate Driver Violin Plot Title", {
    tbl <- dplyr::tibble(
        p_value = 0.000111,
        log10_fold_change = 2.111111,
        label = "G1; A:B"
    )
    expect_equal(
        create_md_violin_plot_title(tbl, "Across groups"),
        "P-value: 1e-04 ; Log10(Fold Change): 2.1111"
    )
    expect_equal(
        create_md_violin_plot_title(tbl, "By group"),
        "Group: G1 ; P-value: 1e-04 ; Log10(Fold Change): 2.1111"
    )
})
