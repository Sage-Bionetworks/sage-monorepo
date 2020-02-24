get_til_map_named_list <- function(){

    subquery <- "SELECT id FROM classes WHERE name = 'TIL Map Characteristic'"

    query <- paste(
        "SELECT display, id AS feature FROM features",
        "WHERE class_id = (",
        subquery,
        ")"
    )

    query %>%
        perform_query("build feature table") %>%
        tibble::deframe(.)
}

build_tilmap_distplot_tbl <- function(tbl, id, scale_method){

    query <- paste(
        "SELECT sample_id, feature_id, value",
        "FROM features_to_samples",
        "WHERE feature_id =",
        id
    )

    query %>%
        perform_query("build immune feature table") %>%
        dplyr::inner_join(tbl, by = "sample_id") %>%
        dplyr::select(-.data$sample_id, .data$sample_name) %>%
        scale_tbl_value_column(scale_method) %>%
        dplyr::rename(x = .data$group, y = .data$value)
}
