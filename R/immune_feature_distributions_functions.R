
build_ifd_distplot_tbl <- function(cohort_object, feature_name, scale_method){
    cohort_object %>%
        query_feature_values_with_cohort_object(feature = feature_name) %>%
        dplyr::inner_join(cohort_object$sample_tbl, by = "sample") %>%
        dplyr::select("group", "value" = "feature_value") %>%
        scale_tbl_value_column(scale_method) %>%
        dplyr::rename(x = .data$group, y = .data$value)
}
