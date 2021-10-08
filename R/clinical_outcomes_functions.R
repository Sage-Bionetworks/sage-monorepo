build_co_survival_list <- function(tbl){
  tbl %>%
    dplyr::filter(.data$class == "Survival Time") %>%
    dplyr::arrange(.data$order) %>%
    dplyr::select("display", "name") %>%
    tibble::deframe(.)
}

get_co_status_feature <- function(time_feature){
  if (time_feature == "PFI_time_1") return("PFI_1")
  else if (time_feature == "OS_time") return("OS")
  else stop("Unknown time feature")
}

build_co_survival_value_tbl <- function(cohort_obj, time, status) {

  time_tbl <-
    cohort_obj$get_feature_values(time) %>%
    dplyr::select("sample_name", "time" = "feature_value")

  status_tbl <-
    cohort_obj$get_feature_values(status) %>%
    dplyr::select("sample_name", "status" = "feature_value")

  tbl <- cohort_obj$sample_tbl %>%
    dplyr::rename("group" = "group_name") %>%
    dplyr::inner_join(time_tbl, by = "sample_name") %>%
    dplyr::inner_join(status_tbl, by = "sample_name") %>%
    dplyr::select("sample" = "sample_name", "group", "time", "status")

  return(tbl)
}

build_co_feature_tbl <- function(cohort_obj, feature_class){
  tbl <-
    cohort_obj$get_feature_values(feature_classes = feature_class) %>%
    dplyr::select(
      "sample" = "sample_name",
      "feature_display",
      "feature_value",
      "feature_order"
    )
}

build_co_heatmap_tbl <- function(survival_tbl, feature_tbl){
  survival_tbl %>%
    dplyr::inner_join(feature_tbl, by = "sample") %>%
    dplyr::select(
      "sample",
      "group",
      "time",
      "status",
      "feature_display",
      "feature_value",
      "feature_order"
    )
}

build_co_heatmap_matrix <- function(tbl){
  tbl %>%
    dplyr::select(
      "feature" = "feature_display",
      "value" = "feature_value",
      "time",
      "status",
      "group",
      "order" = "feature_order"
    ) %>%
    tidyr::nest(
      value = .data$value,
      data = c(.data$time, .data$status)
    ) %>%
    dplyr::mutate(
      value = purrr::map(.data$value, as.matrix),
      data = purrr::map(.data$data, as.matrix)
    ) %>%
    dplyr::mutate(result = purrr::map2_dbl(
      .data$value,
      .data$data,
      concordanceIndex::concordanceIndex
    )) %>%
    dplyr::arrange(.data$order) %>%
    dplyr::select("feature", "group", "result") %>%
    tidyr::pivot_wider(
      .data$feature,
      names_from = .data$group,
      values_from = .data$result
    ) %>%
    as.data.frame() %>%
    dplyr::select(sort(names(.))) %>%
    tibble::column_to_rownames("feature") %>%
    as.matrix()
}
