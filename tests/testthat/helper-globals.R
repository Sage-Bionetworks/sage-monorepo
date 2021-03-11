read_rds_file <- function(file){
  test_file_dir  <- system.file("test_rds_files", package = "iatlas.app")
  readRDS(file.path(test_file_dir, file))
}

tcga_samples <- iatlas.api.client::query_dataset_samples("TCGA")$name
pcawg_samples <- iatlas.api.client::query_dataset_samples("PCAWG")$name

pcawg_immune_subtype_cohort_obj <- read_rds_file(
  "pcawg_immune_subtype_cohort_obj.rds"
)

tcga_immune_subtype_cohort_obj <- read_rds_file(
  "tcga_immune_subtype_cohort_obj.rds"
)

pcawg_feature_bin_cohort_obj <- read_rds_file(
  "pcawg_feature_bin_cohort_obj.rds"
)

tcga_feature_bin_cohort_obj <- read_rds_file(
  "tcga_feature_bin_cohort_obj.rds"
)

tcga_immune_subtype_cohort_obj_50 <- read_rds_file(
  "tcga_immune_subtype_cohort_obj_50.rds"
)

tcga_study_cohort_obj_50 <- read_rds_file(
  "tcga_study_cohort_obj_50.rds"
)

tcga_feature_bin_cohort_obj_50 <- read_rds_file(
  "tcga_feature_bin_cohort_obj_50.rds"
)

