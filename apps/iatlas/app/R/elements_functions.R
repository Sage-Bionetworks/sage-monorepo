
build_tag_filter_list <- function(parent_tag_name, dataset){
  iatlas.api.client::query_tags(
    datasets = dataset,
    parent_tags = parent_tag_name
  ) %>%
    dplyr::pull("name")
}
