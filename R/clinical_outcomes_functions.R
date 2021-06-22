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
    iatlas.modules2::query_feature_values_with_cohort_object(
      cohort_object = cohort_obj,
      features = time
    ) %>%
    dplyr::select("sample", "time" = "feature_value")

  status_tbl <-
    iatlas.modules2::query_feature_values_with_cohort_object(
      cohort_object = cohort_obj,
      features = status
    ) %>%
    dplyr::select("sample", "status" = "feature_value")

  tbl <-
    purrr::reduce(
      list(cohort_obj$sample_tbl, time_tbl, status_tbl),
      dplyr::inner_join,
      by = "sample"
    ) %>%
    dplyr::select("sample", "group", "time", "status")

  return(tbl)
}

build_co_feature_tbl <- function(cohort_obj, feature_class){
  tbl <-
    iatlas.modules2::query_feature_values_with_cohort_object(
      cohort_object = cohort_obj,
      feature_class = feature_class
    ) %>%
    dplyr::select(
      "sample",
      "feature_display",
      "feature_value",
      "feature_order"
    )
}

build_co_heatmap_tbl <- function(survival_tbl, feature_tbl, sample_tbl){
  survival_tbl %>%
    dplyr::select(-"group") %>%
    dplyr::inner_join(feature_tbl, by = "sample") %>%
    dplyr::inner_join(sample_tbl, by = "sample") %>%
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
