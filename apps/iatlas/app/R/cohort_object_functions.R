
show_co_submodules <- function(cohort_obj){
  iatlas.modules2::cohort_has_classes(cohort_obj, c("Survival Time", "Survival Status"))
}

show_ud_submodule <- function(cohort_obj){
  all(
    iatlas.modules2::cohort_has_dataset(cohort_obj, "TCGA"),
    iatlas.modules2::cohort_has_group(
      cohort_obj, c("Immune_Subtype", "TCGA_Subtype", "TCGA_Study")
    )
  )
}

show_md_submodule <- function(cohort_obj){
  iatlas.modules2::cohort_has_dataset(cohort_obj, "TCGA")
}

show_tilmap_submodules <- function(cohort_obj){
  iatlas.modules2::cohort_has_classes(cohort_obj, "TIL Map Characteristic")
}

show_ocp_submodule <- function(cohort_obj){
  iatlas.modules2::cohort_has_features(
    cohort_object = cohort_obj,
    features = c("leukocyte_fraction", "Stromal_Fraction", "Tumor_fraction")
  )
}

show_ecn_submodules <- function(cohort_obj){
  any(
    all(
      iatlas.modules2::cohort_has_dataset(cohort_obj, "TCGA"),
      iatlas.modules2::cohort_has_group(
        cohort_obj, c("Immune_Subtype", "TCGA_Subtype", "TCGA_Study")
      )
    ),
    all(
      iatlas.modules2::cohort_has_dataset(cohort_obj, "PCAWG"),
      iatlas.modules2::cohort_has_group(
        cohort_obj, c("Immune_Subtype", "PCAWG_Study")
      )
    )
  )

}

show_ctf_submodule <- function(cohort_obj){
  fraction_classes <- c(
    "Immune Cell Proportion - Common Lymphoid and Myeloid Cell Derivative Class",
    "Immune Cell Proportion - Differentiated Lymphoid and Myeloid Cell Derivative Class",
    "Immune Cell Proportion - Multipotent Progenitor Cell Derivative Class",
    "Immune Cell Proportion - Original"
  )
  iatlas.modules2::cohort_has_classes(
    cohort_object = cohort_obj,
    classes = fraction_classes,
    all_classes = F
  )
}
