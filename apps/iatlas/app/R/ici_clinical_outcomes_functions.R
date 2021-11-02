build_survival_df <- function(df, group_column, time_column) {

  if (time_column == "OS_time") {
    df <- df %>%
      dplyr::filter(feature_name %in% c("OS", "OS_time")) %>%
      dplyr::select(sample_name, group_column, dataset_name, feature_name, feature_value) %>%
      tidyr::pivot_wider(names_from = feature_name, values_from = feature_value)
    time_status <-  "OS"
  } else {
    df <- df %>%
      dplyr::filter(feature_name %in% c("PFI_1", "PFI_time_1")) %>%
      dplyr::select(sample_name, group_column, dataset_name, feature_name, feature_value) %>%
      tidyr::pivot_wider(names_from = feature_name, values_from = feature_value)
    time_status <-  "PFI_1"
  }

  if(nrow(df) == 0) return(NULL)

  data.frame(
    status = purrr::pluck(df, time_status),
    time = purrr::pluck(df, time_column),
    measure = purrr::pluck(df, group_column)
  ) %>%
    na.omit()
}

get_group_colors <- function(cohort_obj){
  if("Immune feature bin range" %in% cohort_obj$group_tbl$characteristics){
    group_colors <- viridis::viridis(dplyr::n_distinct(cohort_obj$sample_tbl$group_name))
    names(group_colors) <- sapply(unique(cohort_obj$sample_tbl$group_name), function(a) paste('measure=',a,sep=''))
  }else{
    group_colors <- unique(cohort_obj$group_tbl$color)
    names(group_colors) <- sapply(unique(cohort_obj$group_tbl$short_name), function(a) paste('measure=',a,sep=''))
  }
  group_colors
}
