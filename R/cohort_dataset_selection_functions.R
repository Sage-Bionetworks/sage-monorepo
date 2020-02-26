create_cohort_module_string <- function(tbl, .dataset){
    tbl %>%
        dplyr::filter(.data$dataset == .dataset) %>%
        dplyr::pull(.data$module) %>%
        stringr::str_c(collapse = ", ") %>%
        stringr::str_c(
            "Modules available for dataset :",
            .dataset,
            "are",
            .,
            sep = " "
        )
}
