### cohort as input -----------------------------------------------------------
## extract from cohort --------------------------------------------------------
get_cohort_feature_class_list <- function(cohort_obj){
  cohort_obj %>%
    purrr::pluck("feature_tbl") %>%
    dplyr::pull("class") %>%
    unique() %>%
    sort()
}

## check cohort for module display --------------------------------------------

cohort_has_classes <- function(cohort_obj, classes, all_classes = T){
  cohort_classes <- cohort_obj %>%
    purrr::pluck("feature_tbl") %>%
    dplyr::pull(.data$class)
  if(all_classes) has_function <- base::all
  else has_function <- base::any
  has_function(classes %in% cohort_classes)
}

cohort_has_features <- function(cohort_obj, features, all_features = T){
  cohort_features <- cohort_obj %>%
    purrr::pluck("feature_tbl") %>%
    dplyr::pull(.data$name)
  if(all_features) has_function <- base::all
  else has_function <- base::any
  has_function(features %in% cohort_features)
}

cohort_has_dataset <- function(cohort_object, datasets){
  cohort_object$dataset %in% datasets
}

cohort_has_group <- function(cohort_object, groups){
  cohort_object$group_name %in% groups
}

show_co_submodules <- function(cohort_obj){
  cohort_has_classes(cohort_obj, c("Survival Time", "Survival Status"))
}

show_ud_submodule <- function(cohort_obj){
  all(
    cohort_has_dataset(cohort_obj, "TCGA"),
    cohort_has_group(
      cohort_obj, c("Immune_Subtype", "TCGA_Subtype", "TCGA_Study")
    )
  )
}

show_md_submodule <- function(cohort_obj){
  cohort_has_dataset(cohort_obj, "TCGA")
}

show_tilmap_submodules <- function(cohort_obj){
  cohort_has_classes(cohort_obj, "TIL Map Characteristic")
}

show_ocp_submodule <- function(cohort_obj){
  cohort_has_features(
    cohort_obj,
    c("leukocyte_fraction", "Stromal_Fraction", "Tumor_fraction")
  )
}

show_ctf_submodule <- function(cohort_obj){
  fraction_classes <- c(
    "Immune Cell Proportion - Common Lymphoid and Myeloid Cell Derivative Class",
    "Immune Cell Proportion - Differentiated Lymphoid and Myeloid Cell Derivative Class",
    "Immune Cell Proportion - Multipotent Progenitor Cell Derivative Class",
    "Immune Cell Proportion - Original"
  )
  cohort_has_classes(cohort_obj, fraction_classes, all_classes = F)
}

## api queries ----------------------------------------------------------------
# features --------------------------------------------------------------------

#' Title
#'
#' @param cohort_object
#' @param feature
#' @param class
#'
#' @import iatlas.api.client
query_feature_values_with_cohort_object <- function(
  cohort_object,
  feature = NA,
  class = NA
){
  if (cohort_object$group_type == "tag") related <- cohort_object$group_name
  else related <- NA
  iatlas.api.client::query_feature_values(
    datasets = cohort_object$dataset,
    parent_tags = related,
    features = feature,
    feature_classes = class,
    samples = cohort_object$sample_tbl$sample
  )
}

# genes -----------------------------------------------------------------------

query_gene_expression_with_cohort_object <- function(
  cohort_object,
  gene_types = NA,
  entrez = NA
){
  iatlas.api.client::query_expression_by_genes(
    gene_types = gene_types,
    entrez = entrez,
    samples = cohort_object$sample_tbl$sample
  )
}
