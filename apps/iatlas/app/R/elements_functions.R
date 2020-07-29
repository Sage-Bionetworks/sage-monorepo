#' #' Build Numeric Filter Tibble
#' #'
#' #' @param feature_id An integer in the feature_id column of the
#' #' features_to_samples table
#' #' @importFrom magrittr %>%
#' #' @importFrom dplyr summarise
#' #' @importFrom rlang .data
#' build_numeric_filter_tbl <- function(feature_id){
#'     feature_id %>%
#'         build_feature_value_tbl_from_ids() %>%
#'         dplyr::summarise(
#'             feature_max = max(.data$value),
#'             feature_min = min(.data$value)
#'         )
#' }

#' Build Tag Filter Named List
#'
#' @param parent_tag_name A string
#' @importFrom magrittr %>%
#' @importFrom tibble deframe
build_tag_filter_list <- function(parent_tag_name, dataset){
    query_tags(dataset, parent_tag_name) %>%
        dplyr::pull("name")
}
