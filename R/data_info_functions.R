#' Create Data Info Class List
#'
#' @importFrom magrittr %>%
create_data_info_class_list <- function(){
    class_list <-
        create_class_list() %>%
        c("All classes" = -1L, .)
    return(class_list)
}

#' Create Feature Tibble
#'
#' @param class_choice_id An integer in class_id column of the features table,
#' or -1
#' @importFrom magrittr %>%
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

    perform_query(query)
}

#' Format Feature Tibble
#'
#' @param feature_tbl A tibble with columns feature, unit and class
#' @importFrom dplyr select
#' @importFrom rlang .data
format_feature_tbl <- function(feature_tbl){
    dplyr::select(
        feature_tbl,
        `Feature Name`   = .data$feature,
        `Variable Class` = .data$class,
        Unit             = .data$unit
    )
}

#' Filter Feature Tibble
#'
#' @param feature_tbl A tibble with columns class_id
#' @param selected_row An integer that is a row number in the tibble
#' @importFrom magrittr %>%
#' @importFrom dplyr slice pull filter
#' @importFrom rlang .data
filter_feature_tbl <- function(feature_tbl, selected_row){
    id <- feature_tbl %>%
        dplyr::slice(selected_row) %>%
        dplyr::pull(.data$class_id)

     dplyr::filter(feature_tbl, .data$class_id == id)
}

#' Format Filtered Feature Tibble
#'
#' @param filtered_feature_tbl A tibble with columns order, feature, unit, and
#' method_tag
#' @importFrom dplyr select
#' @importFrom rlang .data
format_filtered_feature_tbl <- function(filtered_feature_tbl){
    dplyr::select(
        filtered_feature_tbl,
        `Variable Class Order` = .data$order,
        `Feature Name`         = .data$feature,
        Unit                   = .data$unit,
        `Methods Tag`          = .data$method_tag
    )
}

