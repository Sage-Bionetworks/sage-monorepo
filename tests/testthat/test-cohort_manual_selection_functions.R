tcga_samples_50 <- "TCGA" %>%
  iatlas.api.client::query_dataset_samples() %>%
  dplyr::slice(1:50) %>%
  dplyr::pull("name")

pcawg_samples_50 <- "PCAWG" %>%
  iatlas.api.client::query_dataset_samples() %>%
  dplyr::slice(1:50) %>%
  dplyr::pull("name")

# tags --------------------------------------------------------------------

test_that("build_cohort_tbl_by_tag", {
  expected_names <- c(
    "name",
    "group",
    "characteristics",
    "color",
    "sample",
    "size"
  )

  result1 <- build_cohort_tbl_by_tag(
    "TCGA", tcga_samples_50, "TCGA_Study"
  )
  result2 <- build_cohort_tbl_by_tag(
    "PCAWG", pcawg_samples_50, "PCAWG_Study"
  )
  expect_named(result1, expected_names)
  expect_named(result2, expected_names)
})

test_that("build_tag_cohort_object", {
  res1 <- build_tag_cohort_object("TCGA", tcga_samples_50, "TCGA_Study", "TCGA Study")
  res2 <- build_tag_cohort_object( "PCAWG", pcawg_samples_50, "PCAWG_Study", "PCAWG Study")
  expected_names <- c(
    "sample_tbl", "group_tbl", "feature_tbl", "group_name", "group_display"
  )
  expected_group_names <- c(
    "name", "group", "characteristics", "color", "size"
  )
  expected_sample_names <- c("sample", "group")

  expect_named(res1, expected_names)
  expect_named(res2, expected_names)
  expect_named(res1$sample_tbl, expected_sample_names)
  expect_named(res2$sample_tbl, expected_sample_names)
  expect_named(res1$group_tbl, expected_group_names)
  expect_named(res2$group_tbl, expected_group_names)
})

# clinical --------------------------------------------------------------------

test_that("build_clinical_cohort_object", {
  res1 <- build_clinical_cohort_object("TCGA", tcga_samples_50, "gender", "Gender")
  expected_names <- c(
    "sample_tbl", "group_tbl", "feature_tbl", "group_name", "group_display"
  )
  expected_group_names <- c(
    "name", "group", "characteristics", "color", "size"
  )
  expected_sample_names <- c("sample", "group")

  expect_named(res1, expected_names)
  expect_named(res1$sample_tbl, expected_sample_names)
  expect_named(res1$group_tbl, expected_group_names)
})


# mutation status -------------------------------------------------------------

test_that("build_mutation_cohort_object", {
  res1 <- build_mutation_cohort_object("TCGA", tcga_samples_50, 191)
  res2 <- build_mutation_cohort_object("TCGA", tcga_samples_50, "191")

  expected_names <-  c(
    "sample_tbl", "group_tbl", "feature_tbl", "group_name", "group_display"
  )
  expected_sample_tbl_cols <- c("sample", "group")
  expected_group_tbl_cols <- c("group", "size", "name", "characteristics", "color")

  expect_named(res1, expected_names)
  expect_named(res1$sample_tbl, expected_sample_tbl_cols)
  expect_named(res1$group_tbl, expected_group_tbl_cols)
})

# feature bins ------------------------------------------------------------

feature_bin_sample_tbl1 <- build_feature_bin_sample_tbl(
  "TCGA", tcga_samples_50, "leukocyte_fraction", 2L
)

feature_bin_sample_tbl2 <- build_feature_bin_sample_tbl(
  "PCAWG", pcawg_samples_50, "B_cells_Aggregate2", 2L
)

test_that("Build Feature Bin Sample Tibble", {
  expect_named(feature_bin_sample_tbl1, c("sample", "group"))
  expect_equal(length(unique(feature_bin_sample_tbl1$group)), 2L)
  expect_named(feature_bin_sample_tbl2, c("sample", "group"))
  expect_equal(length(unique(feature_bin_sample_tbl2$group)), 2L)
})

test_that("Build Feature Bin Group Tibble", {
  res1 <- build_feature_bin_group_tbl(
    feature_bin_sample_tbl1, "leukocyte_fraction"
  )
  expect_named(res1, c("group", "size", "name", "characteristics"))
  res2 <- build_feature_bin_group_tbl(
    feature_bin_sample_tbl1, "B_cells_Aggregate2"
  )
  expect_named(res2, c("group", "size", "name", "characteristics"))
})

test_that("Build Feature Bin Cohort Object",{

  expected_names <-  c(
    "sample_tbl", "group_tbl", "feature_tbl", "group_name", "group_display"
  )
  expected_sample_tbl_cols <- c("sample", "group")
  expected_group_tbl_cols <- c("group", "size", "name", "characteristics", "color")

  res1 <- build_feature_bin_cohort_object(
    "TCGA", tcga_samples_50, "leukocyte_fraction", 2L
  )
  res2 <- build_feature_bin_cohort_object(
    "PCAWG", pcawg_samples_50, "B_cells_Aggregate2", 2L
  )

  expect_named(res1, expected_names)
  expect_named(res2, expected_names)
  expect_named(res1$sample_tbl, expected_sample_tbl_cols)
  expect_named(res2$sample_tbl, expected_sample_tbl_cols )
  expect_named(res1$group_tbl, expected_group_tbl_cols)
  expect_named(res2$group_tbl, expected_group_tbl_cols)
})

# top level cohort function -----------------------------------------------

expected_cohort_object_names <- c(
  "dataset",
  "dataset_display",
  "feature_tbl",
  "filters",
  "group_name",
  "group_display",
  "group_tbl",
  "group_type",
  "plot_colors",
  "sample_tbl"
)

test_that("Create Cohort Object", {

  expected_sample_names <- c("sample", "group")
  expected_group_names <- c(
    "group",
    "name",
    "characteristics",
    "size",
    "color"
  )
  expected_feature_names <- c(
    "class",
    "display",
    "method_tag",
    "name",
    "order",
    "unit"
  )

  res1 <- build_cohort_object(
    dataset = "TCGA",
    samples = tcga_samples_50,
    group_name = "TCGA_Study",
    group_display = "TCGA Study",
    group_type = "tag"
  )
  res2 <- build_cohort_object(
    dataset = "PCAWG",
    samples = pcawg_samples_50,
    group_name = "PCAWG_Study",
    group_display = "PCAWG Study",
    group_type = "tag"
  )
  res3 <- build_cohort_object(
    dataset = "TCGA",
    samples = tcga_samples_50,
    group_name = "Driver Mutation",
    group_display = "Driver Mutation",
    group_type = "custom",
    mutation_id = 191L
  )
  res4 <- build_cohort_object(
    dataset = "TCGA",
    samples = tcga_samples_50,
    group_name = "Immune Feature Bins",
    group_display = "Immune Feature Bins",
    group_type = "custom",
    bin_immune_feature = "leukocyte_fraction",
    bin_number = 2L
  )

  expect_named(res1, expected_cohort_object_names, ignore.order = T)
  expect_named(res2, expected_cohort_object_names, ignore.order = T)
  expect_named(res3, expected_cohort_object_names, ignore.order = T)
  expect_named(res4, expected_cohort_object_names, ignore.order = T)
  expect_named(res1$group_tbl, expected_group_names, ignore.order = T)
  expect_named(res2$group_tbl, expected_group_names, ignore.order = T)
  expect_named(res3$group_tbl, expected_group_names, ignore.order = T)
  expect_named(res4$group_tbl, expected_group_names, ignore.order = T)
  expect_named(res1$feature_tbl, expected_feature_names, ignore.order = T)
  expect_named(res2$feature_tbl, expected_feature_names, ignore.order = T)
  expect_named(res3$feature_tbl, expected_feature_names, ignore.order = T)
  expect_named(res4$feature_tbl, expected_feature_names, ignore.order = T)

  expect_error(
    build_cohort_object(
      dataset = "TCGA",
      samples = tcga_samples_50,
      group_name = "TCGA_Study",
      group_display = "TCGA Study",
      group_type = "not_type"
    ),
    "not_type is not an allowed group type."
  )
  expect_error(
    build_cohort_object(
      dataset = "TCGA",
      samples = tcga_samples_50,
      group_name = "TCGA_Study",
      group_display = "TCGA Study",
      group_type = "custom"
    ),
    "TCGA_Study is not an allowed custom group name."
  )
})

test_that("build_cohort_object_from_objects", {
  filter_object1 <- list(samples = tcga_samples_50)
  filter_object2 <- list(samples = tcga_samples_50, filters = list())
  group_obj1 <- list(
    dataset = "TCGA",
    group_name = "Immune_Subtype",
    group_display = "Immune Subtype",
    group_type = "tag"
  )
  res1 <- build_cohort_object_from_objects(group_obj1, filter_object1)
  res2 <- build_cohort_object_from_objects(group_obj1, filter_object2)

  expect_named(res1, expected_cohort_object_names, ignore.order = T)
  expect_named(res2, expected_cohort_object_names, ignore.order = T)
})

test_that("add_plot_colors_to_tbl", {
  tbl1 <- dplyr::tribble(
    ~group, ~size, ~name,       ~characteristics,
    "Mut",  2L,    "EZH2:(NS)", "Mutation Status",
    "Wt",   30L,   "EZH2:(NS)", "Mutation Status"
  )
  res1 <- add_plot_colors_to_tbl(tbl1)
  res2 <- add_plot_colors_to_tbl(dplyr::tibble("group" = c("C1", "C2", "C3")))
  res3 <- add_plot_colors_to_tbl(dplyr::tibble("group" = "C1"))
  expect_named(res1, c("group", "size", "name", "characteristics", "color"))
  expect_named(res2, c("group", "color"))
  expect_named(res3, c("group", "color"))
})

