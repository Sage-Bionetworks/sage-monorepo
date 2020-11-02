build_cohort_object_from_objects <- function(group_object, filter_object){
  purrr::invoke(build_cohort_object, c(group_object, filter_object))
}

#' Build Cohort Object
#'
#' @param filter_obj A named list with element sample_ids and filters
#' @param group_choice A string
#' @param dataset A string
#' @param driver_mutation A string
#' @param immune_feature_bin_id An integer
#' @param immune_feature_bin_number An integer
#' @importFrom purrr list_modify
build_cohort_object <- function(
  dataset,
  samples,
  group_name,
  group_type,
  mutation_id = NA,
  bin_immune_feature = NA,
  bin_number = NA,
  filters = NA
){
  if(group_type == "tag"){
    cohort_object <- build_tag_cohort_object(dataset, samples, group_name)
  } else if(group_type == "custom"){
    if(group_name == "Driver Mutation"){
      cohort_object <- build_mutation_cohort_object(
        dataset, samples, mutation_id
      )
    } else if (group_name == "Immune Feature Bins") {
      cohort_object <- build_feature_bin_cohort_object(
        dataset, samples, bin_immune_feature, bin_number
      )
    } else {
      stop(group_name, " is not an allowed custom group name.")
    }
  } else {
    stop(group_type, " is not an allowed group type.")
  }
  cohort_object$dataset <- dataset
  cohort_object$group_type <- group_type
  cohort_object$filters <- filters
  cohort_object$plot_colors <- cohort_object$group_tbl %>%
    dplyr::select("group", "color") %>%
    tibble::deframe(.)
  return(cohort_object)
}

# tag choice ------------------------------------------------------------------

#' Build Tag Cohort Object
#'
#' @param sample_ids A vector of integers
#' @param group_choice A string
#' @importFrom magrittr %>%
#' @importFrom dplyr select
#' @importFrom rlang .data
build_tag_cohort_object <- function(dataset, samples, tag){
  cohort_tbl  <- build_cohort_tbl_by_tag(dataset, samples, tag)
  sample_tbl   <- cohort_tbl %>%
    dplyr::select("sample", "group") %>%
    dplyr::arrange(.data$sample)
  group_tbl <- cohort_tbl %>%
    dplyr::select(-"sample") %>%
    dplyr::distinct() %>%
    dplyr::arrange(.data$group) %>%
    add_plot_colors_to_tbl(.) %>%
    dplyr::select("name", "group", "characteristics", "color", "size")
  feature_tbl <- iatlas.api.client::query_features(
    datasets = dataset, samples = samples
  )
  list(
    "sample_tbl"  = sample_tbl,
    "group_tbl"   = group_tbl,
    "feature_tbl" = feature_tbl,
    "group_name"  = tag
  )
}

#' Build Cohort Tibble By Tag
#'
#' @param sample_ids Integers in the id column of the samples table
#' @param group A String that is the display column of the tags table
#' @importFrom magrittr %>%
build_cohort_tbl_by_tag <- function(dataset, samples, tag){
  tbl <-
    iatlas.api.client::query_tag_samples(
      datasets = dataset,
      parent_tags = tag,
      samples = samples
    ) %>%
    dplyr::select(
      "name" = "long_display",
      "group" = "short_display",
      "characteristics",
      "color",
      "sample" = "samples",
      "size"
    ) %>%
    tidyr::unnest(cols = "sample")
}

# mutation choice -------------------------------------------------------------

# TODO: Fix after mutation status can be queried
#' Build Driver Mutation Cohort Object
#'
#' @param sample_ids A vector of integers
#' @param mutation A string
#' @importFrom magrittr %>%
#' @importFrom dplyr select
#' @importFrom rlang .data
build_mutation_cohort_object <- function(dataset, samples, mutation_id){

  mutation <- iatlas.api.client::query_mutations(ids = as.integer(mutation_id)) %>%
    dplyr::mutate("mutation" = stringr::str_c(.data$hgnc, ":", .data$code)) %>%
    dplyr::pull(mutation) %>%
    unique()

  sample_tbl <-
    iatlas.api.client::query_samples_by_mutation_status(
      mutation_ids = as.integer(mutation_id),
      samples = samples
    ) %>%
    dplyr::select("sample", "group" = "status")

  group_tbl <- create_mutation_cohort_group_tbl(sample_tbl, mutation) %>%
    add_plot_colors_to_tbl()

  feature_tbl <- iatlas.api.client::query_features(
    datasets = dataset, samples = samples
  )
  list(
    "sample_tbl"  = sample_tbl,
    "group_tbl"   = group_tbl,
    "feature_tbl" = feature_tbl,
    "group_name"  = paste0("Mutation Status: ", mutation)
  )
}

#' Create Driver Mutation Cohort Group Tibble
#'
#' @param sample_tbl A tibble
#' @param gene_name A string
#' @importFrom magrittr %>%
#' @importFrom dplyr group_by summarise ungroup arrange n mutate
#' @importFrom rlang .data
create_mutation_cohort_group_tbl <- function(sample_tbl, mutation){
  sample_tbl %>%
    dplyr::group_by(.data$group) %>%
    dplyr::summarise(size = dplyr::n(), .groups = "drop") %>%
    dplyr::mutate(
      "name" = mutation,
      "characteristics" = "Mutation Status"
    ) %>%
    dplyr::arrange(.data$group)
}

# immune feature bin choice ---------------------------------------------------

#' Build Feature Bin Cohort Object
#'
#' @param sample_ids A vector of integers
#' @param feature_id An integer
#' @param bin_number An integer
#' @importFrom magrittr %>%
#' @importFrom dplyr select
#' @importFrom rlang .data
build_feature_bin_cohort_object <- function(
  dataset,
  samples,
  feature_name,
  bin_number
){

  feature_display <- feature_name %>%
    iatlas.api.client::query_features(features = .) %>%
    dplyr::pull("display")

  sample_tbl <- build_feature_bin_sample_tbl(
    dataset,
    samples,
    feature_name,
    bin_number
  )

  group_tbl <- build_feature_bin_group_tbl(sample_tbl, feature_display) %>%
    add_plot_colors_to_tbl()

  feature_tbl <- iatlas.api.client::query_features(
    datasets = dataset, samples = samples
  )

  list(
    "sample_tbl"  = sample_tbl,
    "group_tbl"   = group_tbl,
    "feature_tbl" = feature_tbl,
    "group_name"  = paste("Immune Feature Bins:", feature_display)
  )
}

# TODO use samples in api query
#' Build Feature Bin Sample Tibble
#'
#' @param sample_ids A vector of integers
#' @param feature_id An integer
#' @param n_bins An integer
#' @importFrom magrittr %>%
#' @importFrom dplyr select mutate
#' @importFrom rlang .data
build_feature_bin_sample_tbl <- function(
  dataset, samples, feature_name, n_bins
){
  res <-
    iatlas.api.client::query_feature_values(
      features = feature_name, datasets = dataset, samples = samples
    ) %>%
    dplyr::filter(.data$sample %in% samples) %>%
    dplyr::mutate("group" = as.character(cut(.data$feature_value, n_bins))) %>%
    dplyr::select("sample", "group")
}



#' Build Feature Bin Group Tibble
#'
#' @param sample_tbl A tibble with name group
#' @param feature_name A string
#' @importFrom magrittr %>%
#' @importFrom dplyr group_by summarise ungroup arrange n mutate
#' @importFrom rlang .data
build_feature_bin_group_tbl <- function(sample_tbl, feature_name){
  sample_tbl %>%
    dplyr::group_by(.data$group) %>%
    dplyr::summarise(size = dplyr::n()) %>%
    dplyr::ungroup() %>%
    dplyr::mutate(
      "name" = feature_name,
      "characteristics" = "Immune feature bin range"
    ) %>%
    dplyr::arrange(.data$group)
}

# various ---------------------------------------------------------------------


add_plot_colors_to_tbl <- function(tbl){
  if ("color" %in% colnames(tbl)){
    if(any(is.na(tbl$color))) {
      tbl <- dplyr::select(tbl, -"color")
    } else {
      return(tbl)
    }
  }
  tbl <- tbl %>%
    dplyr::select("group") %>%
    dplyr::distinct() %>%
    dplyr::arrange(.data$group) %>%
    dplyr::mutate("color" = viridisLite::viridis(dplyr::n())) %>%
    dplyr::inner_join(tbl, ., by = "group")
  return(tbl)

}
