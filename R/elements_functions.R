
#' Build Tag Filter Named List
#'
#' @param parent_tag_name A string
#' @importFrom magrittr %>%
#' @importFrom tibble deframe
build_tag_filter_list <- function(parent_tag_name, dataset){
  iatlas.api.client::query_tags(
    datasets = dataset,
    parent_tags = parent_tag_name
  ) %>%
    dplyr::pull("name")
}
