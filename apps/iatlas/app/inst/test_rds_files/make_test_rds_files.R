
pcawg_immune_subtype_cohort_obj <- build_cohort_object(
  filter_obj = list(
    "samples" = iatlas.api.client::query_dataset_samples("PCAWG")$name
  ),
  dataset = "PCAWG",
  group_choice = "Immune_Subtype",
  group_type = "tag",
  feature_tbl = "PCAWG" %>%
    iatlas.api.client::query_features_by_class() %>%
    dplyr::select("class", "display", "name")
)

saveRDS(
  pcawg_immune_subtype_cohort_obj,
  "inst/test_rds_files/pcawg_immune_subtype_cohort_obj.rds"
)

pcawg_feature_bin_cohort_obj <- build_cohort_object(
  filter_obj = list(
    "samples" = iatlas.api.client::query_dataset_samples("PCAWG")$name
  ),
  dataset = "PCAWG",
  group_choice = "Immune Feature Bins",
  group_type = "custom",
  feature_name = "B_cells_Aggregate2",
  bin_number = 2,
  feature_tbl = "PCAWG" %>%
    iatlas.api.client::query_features_by_class() %>%
    dplyr::select("class", "display", "name")
)

saveRDS(
  pcawg_feature_bin_cohort_obj,
  "inst/test_rds_files/pcawg_feature_bin_cohort_obj.rds"
)

tcga_immune_subtype_cohort_obj <- build_cohort_object(
  filter_obj = list(
    "samples" = iatlas.api.client::query_dataset_samples("TCGA")$name
  ),
  dataset = "TCGA",
  group_choice = "Immune_Subtype",
  group_type = "tag",
  feature_tbl = "TCGA" %>%
    iatlas.api.client::query_features_by_class() %>%
    dplyr::select("class", "display", "name")
)

saveRDS(
  tcga_immune_subtype_cohort_obj,
  "inst/test_rds_files/tcga_immune_subtype_cohort_obj.rds"
)

tcga_immune_subtype_cohort_obj_50 <- build_cohort_object(
  filter_obj = list(
    "samples" = iatlas.api.client::query_dataset_samples("TCGA")$name[1:50]
  ),
  dataset = "TCGA",
  group_choice = "Immune_Subtype",
  group_type = "tag",
  feature_tbl = "TCGA" %>%
    iatlas.api.client::query_features_by_class() %>%
    dplyr::select("class", "display", "name")
)

saveRDS(
  tcga_immune_subtype_cohort_obj_50,
  "inst/test_rds_files/tcga_immune_subtype_cohort_obj_50.rds"
)



tcga_feature_bin_cohort_obj <- build_cohort_object(
  filter_obj = list(
    "samples" = iatlas.api.client::query_dataset_samples("TCGA")$name
  ),
  dataset = "TCGA",
  group_choice = "Immune Feature Bins",
  group_type = "custom",
  feature_name = "leukocyte_fraction",
  bin_number = 2,
  feature_tbl = "TCGA" %>%
    iatlas.api.client::query_features_by_class() %>%
    dplyr::select("class", "display", "name")
)

saveRDS(
  tcga_feature_bin_cohort_obj,
  "inst/test_rds_files/tcga_feature_bin_cohort_obj.rds"
)


tcga_feature_bin_cohort_obj_50 <- build_cohort_object(
  filter_obj = list(
    "samples" = iatlas.api.client::query_dataset_samples("TCGA")$name[1:50]
  ),
  dataset = "TCGA",
  group_choice = "Immune Feature Bins",
  group_type = "custom",
  feature_name = "leukocyte_fraction",
  bin_number = 2,
  feature_tbl = "TCGA" %>%
    iatlas.api.client::query_features_by_class() %>%
    dplyr::select("class", "display", "name")
)

saveRDS(
  tcga_feature_bin_cohort_obj_50,
  "inst/test_rds_files/tcga_feature_bin_cohort_obj_50.rds"
)
