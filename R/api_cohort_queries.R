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
