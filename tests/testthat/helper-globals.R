get_tcga_samples_tbl <- memoise::memoise(
  function(){
    iatlas.api.client::query_cohort_samples(cohorts = "TCGA")
  }
)

get_tcga_study_samples_tbl <- memoise::memoise(
  function(){
    iatlas.api.client::query_cohort_samples(cohorts = "TCGA_TCGA_Study")
  }
)

get_tcga_samples <- memoise::memoise(
  function(){
    get_tcga_samples_tbl()$sample_name
  }
)

get_tcga_samples_50 <- memoise::memoise(
  function(){
    get_tcga_samples()[1:50]
  }
)

get_tcga_features_tbl <- memoise::memoise(
  function(){
    iatlas.api.client::query_features(cohorts = "TCGA")
  }
)

get_pcawg_samples_tbl <- memoise::memoise(
  function(){
    iatlas.api.client::query_cohort_samples(cohorts = "PCAWG")
  }
)

get_pcawg_study_samples_tbl <- memoise::memoise(
  function(){
    iatlas.api.client::query_cohort_samples(cohorts = "PCAWG_PCAWG_Study")
  }
)

get_pcawg_samples <- memoise::memoise(
  function(){
    get_pcawg_samples_tbl()$sample_name
  }
)

get_pcawg_samples_50 <- memoise::memoise(
  function(){
    get_pcawg_samples()[1:50]
  }
)

get_pcawg_features_tbl <- memoise::memoise(
  function(){
    iatlas.api.client::query_features(cohorts = "PCAWG")
  }
)

# ----

read_rds_file <- function(file){
  test_file_dir  <- system.file("test_rds_files", package = "iatlas.modules2")
  readRDS(file.path(test_file_dir, file))
}

get_pcawg_immune_subtype_cohort_obj <- memoise::memoise(
  function(){
    read_rds_file("pcawg_immune_subtype_cohort_obj.rds")
  }
)

get_tcga_immune_subtype_cohort_obj <- memoise::memoise(
  function(){
    read_rds_file("tcga_immune_subtype_cohort_obj.rds")
  }
)

get_pcawg_feature_bin_cohort_obj <- memoise::memoise(
  function(){
    read_rds_file("pcawg_feature_bin_cohort_obj.rds")
  }
)

get_tcga_feature_bin_cohort_obj <- memoise::memoise(
  function(){
    read_rds_file("tcga_feature_bin_cohort_obj.rds")
  }
)

get_tcga_immune_subtype_cohort_obj_50 <- memoise::memoise(
  function(){
    read_rds_file("tcga_immune_subtype_cohort_obj_50.rds")
  }
)

get_tcga_feature_bin_cohort_obj_50 <- memoise::memoise(
  function(){
    read_rds_file("tcga_feature_bin_cohort_obj_50.rds")
  }
)

get_tcga_study_cohort_obj_50 <- memoise::memoise(
  function(){
    read_rds_file("tcga_study_cohort_obj_50.rds")
  }
)



