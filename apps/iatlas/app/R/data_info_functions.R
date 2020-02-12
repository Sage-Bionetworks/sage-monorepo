create_class_list <- function(){
    class_list <-
        .GlobalEnv$create_class_list() %>%
        c("All classes" = -1L, .)
    return(class_list)
}

create_feature_tbl <- function(class_choice_id){
    query <- paste0(
        "SELECT f.display AS feature, f.unit, f.class_id, f.order, ",
        "c.name AS class, m.name AS method_tag FROM features f ",
        "INNER JOIN classes c ON f.class_id = c.id ",
        "INNER JOIN method_tags m ON f.method_tag_id = m.id "
    )
    if (class_choice_id != -1L) {
        query <- paste0(query, " WHERE class_id = ", class_choice_id)
    }
    query <- paste0(query, " ORDER BY c.name, f.order, f.display")

    .GlobalEnv$perform_query(query)
}

format_feature_tbl <- function(feature_tbl){
    dplyr::select(
        feature_tbl,
        `Feature Name`   = .data$feature,
        `Variable Class` = .data$class,
        Unit             = .data$unit
    )
}

filter_feature_tbl <- function(feature_tbl, selected_row){
    id <- feature_tbl %>%
        dplyr::slice(selected_row) %>%
        dplyr::pull(.data$class_id)

     dplyr::filter(feature_tbl, .data$class_id == id)
}

format_filtered_feature_tbl <- function(filtered_feature_tbl){
    dplyr::select(
        filtered_feature_tbl,
        `Variable Class Order` = .data$order,
        `Feature Name`         = .data$feature,
        Unit                   = .data$unit,
        `Methods Tag`          = .data$method_tag
    )
}

get_selected_method_tags <- function(filtered_feature_tbl){
    filtered_feature_tbl %>%
        dplyr::select(.data$method_tag) %>%
        tidyr::drop_na() %>%
        dplyr::distinct() %>%
        dplyr::pull(.data$method_tag)
}
