
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
