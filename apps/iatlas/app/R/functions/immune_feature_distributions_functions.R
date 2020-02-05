
build_distplot_tbl <- function(tbl, id, scale_method){
    id %>%
        get_feature_values_from_ids() %>%
        dplyr::inner_join(tbl, by = "sample_id") %>%
        dplyr::select(group, value) %>%
        scale_db_connection(scale_method) %>%
        dplyr::rename(x = group, y = value)
}
