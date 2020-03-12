#' Create Tilmap Named List
#'
#' @param tbl A tibble with columns class, display and id
#' @importFrom magrittr %>%
#' @importFrom rlang .data
#' @importFrom dplyr select filter
#' @importFrom tibble deframe
create_tm_named_list <- function(tbl){
    tbl %>%
        dplyr::filter(class == 'TIL Map Characteristic') %>%
        dplyr::select(.data$display, .data$id) %>%
        tibble::deframe(.)
}

#' Build Tilmap Distplot Tibble
#'
#' @param tbl A tibble with columns sample_id, sample_name, slide_barcode, group
#' @param id An integer in the feature_id column of the features_to_samples
#' table
#' @param scale_method A string
#' @importFrom magrittr %>%
#' @importFrom rlang .data
#' @importFrom dplyr select rename inner_join
build_tm_distplot_tbl <- function(tbl, id, scale_method){
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
