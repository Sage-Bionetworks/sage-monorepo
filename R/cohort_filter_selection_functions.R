
is_group_filter_valid <- function(obj){
  all(
    !is.null(obj),
    !is.null(obj$group_choices),
    !is.null(obj$group_type),
    !is.null(obj$parent_group_choice)
  )
}

get_valid_group_filters <- function(filter_obj){
  filter_obj %>%
    purrr::keep(purrr::map_lgl(., is_group_filter_valid)) %>%
    unname()
}

get_group_filtered_samples <- function(filter_obj, samples, dataset){
  tag_filter_samples <-
    purrr::keep(
      filter_obj, purrr::map_lgl(filter_obj, ~.x$group_type == "tag")
    ) %>%
    get_filtered_group_tag_samples(., samples, dataset)

  clinical_filter_samples <-
    purrr::keep(
      filter_obj, purrr::map_lgl(filter_obj, ~.x$group_type == "clinical")
    ) %>%
    get_filtered_group_clinical_samples(., samples)

  purrr::reduce(
    list(tag_filter_samples, clinical_filter_samples),
    base::intersect, .init = samples)

}

get_filtered_group_tag_samples <- function(filter_obj, samples, dataset){
  filter_obj %>%
    purrr::transpose(.) %>%
    purrr::pluck("group_choices") %>%
    purrr::map(
      .,
      ~iatlas.api.client::query_samples_by_tag2(datasets = dataset, tags = .x)
    ) %>%
    purrr::map(., dplyr::pull, "sample") %>%
    purrr::reduce(base::intersect, .init = samples)
}

get_filtered_group_clinical_samples <- function(filter_obj, samples){
  filter_obj %>%
    purrr::map(
      get_filtered_group_clinical_samples_by_filter, samples = samples
    ) %>%
    purrr::reduce(base::intersect, .init = samples)
}

get_filtered_group_clinical_samples_by_filter <- function(filter_list, samples){
  genders     <- NA
  ethnicities <- NA
  races       <- NA
  if(filter_list$parent_group_choice == "gender"){
    genders <- filter_list$group_choices
  }
  if(filter_list$parent_group_choice == "ethnicity"){
    ethnicities <- filter_list$group_choices
  }
  if(filter_list$parent_group_choice == "race"){
    races <- filter_list$group_choices
  }
  samples <-
    iatlas.api.client::query_sample_patients(
      samples     = samples,
      genders     = genders,
      ethnicities = ethnicities,
      races       = races
    ) %>%
    dplyr::pull("sample")
}

is_numeric_filter_valid <- function(obj){
  all(
    !is.null(obj),
    !any(
      is.null(obj$name),
      is.null(obj$min),
      is.null(obj$max),
      is.null(obj$type)
    ),
    all(names(obj) %in% c("name", "min", "max", "type"))
  )
}

get_valid_numeric_filters <- function(filter_obj){
  filter_obj %>%
    purrr::keep(purrr::map_lgl(., is_numeric_filter_valid)) %>%
    unname()
}

get_numeric_filtered_samples <- function(filter_obj, samples, dataset){
  feature_filter_samples <-
    purrr::keep(
      filter_obj, purrr::map_lgl(filter_obj, ~.x$type == "feature")
    ) %>%
    get_numeric_feature_filtered_samples(., samples, dataset)

  clinical_filter_samples <-
    purrr::keep(
      filter_obj, purrr::map_lgl(filter_obj, ~.x$type == "clinical")
    ) %>%
    get_numeric_clnical_filtered_samples(., samples)

  purrr::reduce(
    list(feature_filter_samples, clinical_filter_samples),
    base::intersect,
    .init = samples
  )

}

get_numeric_feature_filtered_samples <- function(filter_obj, samples, dataset){
  filter_obj %>%
    purrr::transpose(.) %>%
    purrr::map(~unlist(.x)) %>%
    purrr::list_modify("type" = NULL) %>%
    purrr::pmap(., get_filtered_samples_by_feature, dataset) %>%
    purrr::reduce(base::intersect, .init = samples)
}

#todo: fix to use cohort
get_filtered_samples_by_feature <- function(name, min, max, dataset){
  iatlas.api.client::query_feature_values(
    cohort = dataset, features = name, max_value = max, min_value = min
  ) %>%
    dplyr::pull(sample)
}

get_numeric_clnical_filtered_samples <- function(filter_obj, samples){
  filter_obj %>%
    purrr::map(
      get_numeric_clnical_filtered_samples_by_filter, samples = samples
    ) %>%
    purrr::reduce(base::intersect, .init = samples)

}

get_numeric_clnical_filtered_samples_by_filter <- function(filter_list, samples){
  max_age_at_diagnosis = NA
  max_height = NA
  max_weight = NA
  min_age_at_diagnosis = NA
  min_height = NA
  min_weight = NA
  if(filter_list$name == "height"){
    min_height <- filter_list$min
    max_height <- filter_list$max
  }
  if(filter_list$name == "weight"){
    min_weight <- filter_list$min
    max_weight <- filter_list$max
  }
  if(filter_list$name == "age_at_diagnosis"){
    min_age_at_diagnosis <- filter_list$min
    max_age_at_diagnosis <- filter_list$max
  }
  samples <-
    iatlas.api.client::query_sample_patients(
      samples = samples,
      max_age_at_diagnosis = max_age_at_diagnosis,
      max_height = max_height,
      max_weight = max_weight,
      min_age_at_diagnosis = min_age_at_diagnosis,
      min_height = min_height,
      min_weight = min_weight,
    ) %>%
    dplyr::pull("sample")
}


