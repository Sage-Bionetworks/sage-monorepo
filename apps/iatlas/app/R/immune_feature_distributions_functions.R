#' Build Immune Feature Distribution Plot Tibble
#'
#' @param sample_tbl A tibble with columns "sample_name", "group"
#' @param feature_name A name in the features table
#' @param scale_method A method for scaling the value column
#' @importFrom magrittr %>%
#' @importFrom dplyr inner_join select rename
#' @importFrom rlang .data
build_ifd_distplot_tbl <- function(sample_tbl, feature_name, scale_method){
    feature_name %>%
        iatlas.app::query_samples_to_feature(.) %>%
        dplyr::inner_join(sample_tbl, by = "sample") %>%
        dplyr::select(.data$group, .data$value) %>%
        scale_tbl_value_column(scale_method) %>%
        dplyr::rename(x = .data$group, y = .data$value)
}
