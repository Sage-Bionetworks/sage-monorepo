#' Build Multivariate Driver Tag Covariate Tibble
#'
#' @param covariates A vector of strings that are in the name column of the tags
#' table
#' @importFrom magrittr %>%
#' @importFrom tidyr pivot_wider drop_na
build_md_tag_covariate_tbl <- function(cohort_obj, cov_obj){
  parent_tags <- cov_obj$categorical_covariates
  if (is.null(parent_tags)) return(NULL)
  tbl <-
    purrr::map(
      parent_tags,
      ~iatlas.api.client::query_samples_by_tag(
        datasets = cohort_obj$dataset,
        samples = cohort_obj$sample_tbl$sample,
        parent_tags = .x
      )
    ) %>%
    purrr::map2(parent_tags, ~dplyr::mutate(.x, "parent_tag" = .y)) %>%
    dplyr::bind_rows() %>%
    dplyr::select("sample", "parent_tag", "tag_name") %>%
    tidyr::pivot_wider(
      ., names_from = "parent_tag", values_from = "tag_name"
    ) %>%
    tidyr::drop_na()
}

#' Build Multivariate Driver Feature Covariate Tibble
#'
#' @param covariates A vector of integers that are in the id column of the
#' features table
#' @importFrom magrittr %>%
#' @importFrom tidyr pivot_wider drop_na
build_md_feature_covariate_tbl <- function(cohort_obj, cov_obj){
  features <- cov_obj$numerical_covariates
  if (is.null(features)) return(NULL)
  tbl <-
    iatlas.api.client::query_feature_values(
      features = features,
      datasets = cohort_obj$dataset,
      samples = cohort_obj$sample_tbl$sample
    ) %>%
    dplyr::select("sample", "feature_name", "feature_value") %>%
    tidyr::pivot_wider(
      ., names_from = "feature_name", values_from = "feature_value"
    ) %>%
    tidyr::drop_na()
}

#' Build Multivariate Driver Covariate Tibble
#'
#' @param cov_obj A list with items named categorical_covariates and
#' numerical_covariates
#' @importFrom purrr discard reduce
#' @importFrom dplyr inner_join
#' @importFrom magrittr %>%
build_md_covariate_tbl <- function(cohort_obj, cov_obj){
  tag_tbl     <- build_md_tag_covariate_tbl(cohort_obj, cov_obj)
  feature_tbl <- build_md_feature_covariate_tbl(cohort_obj, cov_obj)
  tbls <-
    list(tag_tbl, feature_tbl) %>%
    purrr::discard(., purrr::map_lgl(., is.null))
  if (length(tbls) == 0) {
    return(NULL)
  } else {
    return(purrr::reduce(tbls, dplyr::inner_join, by = "sample"))
  }
}

#' Build Multivariate Driver Response Tibble
#'
#' @param feature_id An integer in the feature_id column of the
#' features_to_samples table
#' @importFrom dplyr select
#' @importFrom magrittr %>%
#' @importFrom rlang .data
build_md_response_tbl <- function(cohort_obj, feature){
  tbl <-
    query_feature_values_with_cohort_object(cohort_obj, feature) %>%
    dplyr::inner_join(cohort_obj$sample_tbl, by = "sample") %>%
    dplyr::select("sample", "response" = "feature_value")
}

#' Combine Multivariate Driver Tibbles
#'
#' @param resp_tbl A tibble
#' @param sample_tbl A tibble
#' @param status_tbl A tibble
#' @param cov_tbl A tibble or NULL
#' @param mode A string, either "By group" or "Across groups"
#'
#' @importFrom purrr reduce
#' @importFrom dplyr select mutate
#' @importFrom magrittr %>%
#' @importFrom rlang .data
combine_md_tbls <- function(resp_tbl, status_tbl, sample_tbl, cov_tbl, mode){
  tbl <-
    list(
      dplyr::select(resp_tbl, "sample", "response"),
      dplyr::select(sample_tbl, "sample", "group"),
      dplyr::select(status_tbl, "sample", "mutation", "status"),
      cov_tbl
    ) %>%
    purrr::discard(., purrr::map_lgl(., is.null)) %>%
    purrr::reduce(dplyr::inner_join, by = "sample") %>%
    dplyr::select(-"sample")

    if (mode == "By group") {
      tbl <- dplyr::mutate(tbl, label = paste0(
        .data$group, "; ", .data$mutation
      ))
    } else if (mode == "Across groups") {
      tbl <- dplyr::mutate(tbl, label = paste0(
        .data$mutation
      ))
    }

  tbl <- dplyr::select(tbl, -c(.data$group, .data$mutation))
  return(tbl)
}

#' Filter Multivariate Driver Labels
#'
#' @param tbl A tibble
#' @param min_mutants A positive integer
#' @param min_wildtype A positive integer
#'
#' @importFrom dplyr group_by mutate filter ungroup pull summarise n
#' @importFrom magrittr %>%
#' @importFrom rlang .data
filter_md_labels <- function(tbl, min_mutants, min_wildtype){
    tbl %>%
        dplyr::group_by(.data$label) %>%
        dplyr::mutate(status = dplyr::if_else(
            .data$status == "Wt",
            1L,
            0L
        )) %>%
        dplyr::summarise(n_total = dplyr::n(), n_wt = sum(.data$status)) %>%
        dplyr::mutate(n_mut = .data$n_total - .data$n_wt) %>%
        dplyr::filter(
            .data$n_mut >= local(min_mutants),
            .data$n_wt >= local(min_wildtype),
        ) %>%
        dplyr::ungroup() %>%
        dplyr::pull(.data$label)
}

#' Build Multivariate Driver Pvalue Tibble
#'
#' @param tbl A tibble
#' @param formula_string A string that could be converted to a formula
#'
#' @importFrom dplyr select mutate
#' @importFrom tidyr nest drop_na
#' @importFrom magrittr %>%
#' @importFrom rlang .data
build_md_pvalue_tbl <- function(tbl, formula_string){
    tbl %>%
        tidyr::nest(.tbl = -c(.data$label)) %>%
        dplyr::mutate(p_value = purrr::map_dbl(
            .data$.tbl,
            calculate_lm_pvalue,
            formula_string,
            "statusWt"
        )) %>%
        tidyr::drop_na() %>%
        dplyr::select(-.data$.tbl) %>%
        dplyr::mutate(log10_p_value = -log10(.data$p_value))
}

#' Calculate Linear Model Pvalue
#'
#' @param data A dataframe
#' @param lm_formula A string or formula
#' @param term A string
#'
#' @importFrom stats lm
#' @importFrom magrittr %>% extract extract2
calculate_lm_pvalue <- function(data, lm_formula, term){
    data %>%
        stats::lm(formula = lm_formula, data = .) %>%
        summary() %>%
        magrittr::extract2("coefficients") %>%
        magrittr::extract(term, "Pr(>|t|)") %>%
        as.double()
}

#' Build Multivariate Driver Effect Size Tibble
#'
#' @param tbl A tibble
#'
#' @importFrom dplyr group_by ungroup select mutate rename
#' @importFrom tidyr nest pivot_wider drop_na
#' @importFrom magrittr %>%
#' @importFrom rlang .data
build_md_effect_size_tbl <- function(tbl){
    tbl %>%
        dplyr::select(.data$label, .data$response, .data$status) %>%
        dplyr::group_by(.data$label, .data$status) %>%
        dplyr::summarise(responses = list(.data$response)) %>%
        dplyr::ungroup() %>%
        tidyr::pivot_wider(
            .,
            names_from = .data$status,
            values_from = .data$responses
        ) %>%
        dplyr::rename(g1 = .data$Mut, g2 = .data$Wt) %>%
        tidyr::nest(data = c(.data$g1, .data$g2)) %>%
        dplyr::mutate(fold_change = purrr::map_dbl(
            .data$data,
            get_effect_size_from_tbl
        )) %>%
        dplyr::mutate(log10_fold_change = -log10(.data$fold_change)) %>%
        dplyr::select(-.data$data) %>%
        tidyr::drop_na()
}

#' Get Effect Size From Tibble
#'
#' @param tbl A tibble
#' @param method A function
get_effect_size_from_tbl <- function(tbl, method = calculate_ratio_effect_size){
    method(unlist(tbl$g1), unlist(tbl$g2))
}

#' Calculate Ratio Effect Size
#'
#' @param v1 A numeric vector
#' @param v2 A numeric vector
calculate_ratio_effect_size <- function(v1, v2){
    mean1 <- mean(v1)
    mean2 <- mean(v2)
    if (any(mean1 <= 0, mean2 <= 0)) return(NA)
    mean1 / mean2
}

#' Build Multivariate Driver Violin Tibble
#'
#' @param tbl A tibble with columns label, status and response
#' @param .label A string
#'
#' @importFrom dplyr select filter
#' @importFrom magrittr %>%
#' @importFrom rlang .data
build_md_driver_violin_tbl <- function(tbl, .label){
    tbl %>%
        dplyr::filter(.data$label %in% .label) %>%
        dplyr::select(x = .data$status, y = .data$response)
}

#' Create Multivariate Driver Violin Plot Title
#'
#' @param tbl A one row tibble with columns p_value log10_fold_change and label
#' @param mode A string, either "By group" or "Across groups"
#' @importFrom rlang .data
create_md_violin_plot_title <- function(tbl, mode){
    title <- paste(
        "P-value:",
        round(tbl$p_value, 4), ";",
        "Log10(Fold Change):",
        round(tbl$log10_fold_change, 4)
    )
    if (mode == "By group") {
        group <- tbl$label %>%
            stringr::str_match(., "^([:print:]+);[:print:]+$") %>%
            pluck(2)
        title <- paste("Group:", group, ";", title)
    }
    return(title)
}
