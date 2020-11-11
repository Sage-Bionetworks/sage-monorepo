
pcawg_immune_subtype_cohort_obj <- build_cohort_object(
  dataset = "PCAWG",
  group_name = "Immune_Subtype",
  group_type = "tag",
  samples = iatlas.api.client::query_dataset_samples("PCAWG")$name
)

saveRDS(
  pcawg_immune_subtype_cohort_obj,
  "inst/test_rds_files/pcawg_immune_subtype_cohort_obj.rds",
  version = 2
)

pcawg_feature_bin_cohort_obj <- build_cohort_object(
  dataset = "PCAWG",
  samples = iatlas.api.client::query_dataset_samples("PCAWG")$name,
  group_name = "Immune Feature Bins",
  group_type = "custom",
  bin_immune_feature = "B_cells_Aggregate2",
  bin_number = 2
)

saveRDS(
  pcawg_feature_bin_cohort_obj,
  "inst/test_rds_files/pcawg_feature_bin_cohort_obj.rds",
  version = 2
)

tcga_immune_subtype_cohort_obj <- build_cohort_object(
  dataset = "TCGA",
  samples = iatlas.api.client::query_dataset_samples("TCGA")$name,
  group_name = "Immune_Subtype",
  group_type = "tag"
)

saveRDS(
  tcga_immune_subtype_cohort_obj,
  "inst/test_rds_files/tcga_immune_subtype_cohort_obj.rds",
  version = 2
)

tcga_immune_subtype_cohort_obj_50 <- build_cohort_object(
  dataset = "TCGA",
  samples = iatlas.api.client::query_dataset_samples("TCGA")$name[1:50],
  group_name = "Immune_Subtype",
  group_type = "tag"
)

saveRDS(
  tcga_immune_subtype_cohort_obj_50,
  "inst/test_rds_files/tcga_immune_subtype_cohort_obj_50.rds",
  version = 2
)

tcga_study_cohort_obj_50 <- build_cohort_object(
  dataset = "TCGA",
  samples = iatlas.api.client::query_dataset_samples("TCGA")$name[1:50],
  group_name = "TCGA_Study",
  group_type = "tag"
)

saveRDS(
  tcga_study_cohort_obj_50,
  "inst/test_rds_files/tcga_study_cohort_obj_50.rds",
  version = 2
)

tcga_feature_bin_cohort_obj <- build_cohort_object(
  dataset = "TCGA",
  samples = iatlas.api.client::query_dataset_samples("TCGA")$name,
  group_name = "Immune Feature Bins",
  group_type = "custom",
  bin_immune_feature = "leukocyte_fraction",
  bin_number = 2
)

saveRDS(
  tcga_feature_bin_cohort_obj,
  "inst/test_rds_files/tcga_feature_bin_cohort_obj.rds",
  version = 2
)


tcga_feature_bin_cohort_obj_50 <- build_cohort_object(
  dataset = "TCGA",
  "samples" = iatlas.api.client::query_dataset_samples("TCGA")$name[1:50],
  group_name = "Immune Feature Bins",
  group_type = "custom",
  bin_immune_feature = "leukocyte_fraction",
  bin_number = 2
)

saveRDS(
  tcga_feature_bin_cohort_obj_50,
  "inst/test_rds_files/tcga_feature_bin_cohort_obj_50.rds",
  version = 2
)
