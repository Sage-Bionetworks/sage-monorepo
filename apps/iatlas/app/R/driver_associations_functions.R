show_ud_submodule <- function(cohort_obj){
    all(
        cohort_obj$dataset == "TCGA",
        cohort_obj$group_name %in% c(
            "Immune_Subtype", "TCGA_Subtype", "TCGA_Study"
        )
    )
}

show_md_submodule <- function(cohort_obj){
    cohort_obj$dataset == "TCGA"
}
