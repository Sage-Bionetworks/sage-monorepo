### cohort as input -----------------------------------------------------------

get_cohort_feature_class_list <- function(cohort_obj){
  cohort_obj %>%
    purrr::pluck("feature_tbl") %>%
    dplyr::pull("class") %>%
    unique() %>%
    sort()
}


## api queries ----------------------------------------------------------------
# features --------------------------------------------------------------------

query_feature_values_with_cohort_object <- function(
  cohort_object,
  feature = list(),
  class = list()
){
  if (cohort_object$group_type == "tag") related <- cohort_object$group_name
  else related <- list()
  dataset <- cohort_object$dataset
  query_feature_values(dataset, related, feature, class)
}
