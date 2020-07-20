show_ocp_submodule <- function(cohort_obj){
    needed_fractions <- c(
        'Leukocyte Fraction', 'Stromal Fraction', 'Tumor Fraction'
    )
    available_fractions <- cohort_obj %>%
        purrr::pluck("feature_tbl") %>%
        dplyr::filter(.data$class %in% "Overall Proportion") %>%
        dplyr::pull(.data$display)

    all(needed_fractions %in% available_fractions)
}

show_ctf_submodule <- function(cohort_obj){
    fraction_classes <- c(
        "Immune Cell Proportion - Common Lymphoid and Myeloid Cell Derivative Class",
        "Immune Cell Proportion - Differentiated Lymphoid and Myeloid Cell Derivative Class",
        "Immune Cell Proportion - Multipotent Progenitor Cell Derivative Class",
        "Immune Cell Proportion - Original"
    )
    available_classes <- cohort_obj %>%
        purrr::pluck("feature_tbl") %>%
        dplyr::filter(.data$class %in% fraction_classes) %>%
        dplyr::pull(.data$class)

    length(available_classes) > 0
}
