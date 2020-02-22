build_group_dt_tbl <- function(cohort_obj){
    cohort_obj$plot_colors %>%
        tibble::enframe(., name = "group", value = "color") %>%
        dplyr::inner_join(cohort_obj$group_tbl, by = "group") %>%
        dplyr::select(
            `Sample Group`  = .data$group,
            `Group Name`    = .data$name,
            `Group Size`    = .data$size,
            Characteristics = .data$characteristics,
            `Plot Color`    = .data$color
        )
}
