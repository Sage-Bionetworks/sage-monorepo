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
  feature = NA,
  class = NA
){
  if (cohort_object$group_type == "tag") related <- cohort_object$group_name
  else related <- NA
  query_feature_values(
    feature,
    datasets = cohort_object$dataset,
    parent_tags = related,
    feature_classes = class,
    samples = cohort_object$sample_tbl$sample
  )
}

# genes -----------------------------------------------------------------------

query_gene_expression_with_cohort_object <- function(
  cohort_object,
  gene_types = NA,
  entrez_ids = NA
){
  query_expression_by_genes(
    type = gene_types,
    entrez = entrez_ids,
    sample = cohort_object$sample_tbl$sample
  )
}
