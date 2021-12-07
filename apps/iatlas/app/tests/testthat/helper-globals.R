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
