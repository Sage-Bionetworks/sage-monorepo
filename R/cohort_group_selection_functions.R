build_custom_group_tbl <- function(.dataset){
    dplyr::tribble(
        ~name,                 ~dataset, ~type,
        "Immune Feature Bins", "TCGA",    "custom",
        "Driver Mutation",     "TCGA",    "custom",
        "Immune Feature Bins", "PCAWG",   "custom",
    ) %>%
        dplyr::filter(.data$dataset == .dataset) %>%
        dplyr::mutate("display" = .data$name) %>%
        dplyr::select("display", "name")
}

build_cohort_group_list <- function(tag_group_tbl, custom_group_tbl){
    dplyr::bind_rows(tag_group_tbl, custom_group_tbl) %>%
        dplyr::select("display", "name") %>%
        tibble::deframe(.)
}

build_cohort_mutation_tbl <- function(){
    iatlas.api.client::query_mutations(type = "driver_mutation") %>%
        dplyr::mutate(
            "mutation" = stringr::str_c(.data$hgnc, ":", .data$code)
        )
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
    filter_obj,
    dataset,
    group_choice,
    group_type,
    ...
){
    samples <- filter_obj$samples
    if (group_choice %in% c(
        "Immune_Subtype", "TCGA_Subtype", "TCGA_Study", "PCAWG_Study")
    ) {
        cohort_object <- build_tag_cohort_object(
            dataset, samples, group_choice
        )
        cohort_object$feature_tbl <-
            iatlas.api.client::query_features_by_class(dataset, group_choice)
    } else if (group_choice == "Driver Mutation") {
        cohort_object <- build_dm_cohort_object(dataset, samples, ...)
        cohort_object$feature_tbl <- iatlas.api.client::query_features_by_class(dataset)
    } else if (group_choice == "Immune Feature Bins") {
        cohort_object <- build_feature_bin_cohort_object(dataset, samples, ...)
        cohort_object$feature_tbl <- iatlas.api.client::query_features_by_class(dataset)
    } else {
        stop(group_choice, " is not an allowed group choice.")
    }
    cohort_object$dataset     <- dataset
    cohort_object$filters     <- filter_obj$filters
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
    cohort_tbl  <- build_cohort_tbl_by_tag(samples, dataset, tag)
    sample_tbl   <- cohort_tbl %>%
        dplyr::select("sample", "group") %>%
        dplyr::arrange(.data$sample)
    group_tbl <- cohort_tbl %>%
        dplyr::select(-"sample") %>%
        dplyr::distinct() %>%
        dplyr::arrange(.data$group) %>%
        add_plot_colors_to_tbl(.)

    list(
        "sample_tbl"  = sample_tbl,
        "group_type"  = "tag",
        "group_tbl"   = group_tbl,
        "group_name"  = tag
    )
}

#' Build Cohort Tibble By Tag
#'
#' @param sample_ids Integers in the id column of the samples table
#' @param group A String that is the display column of the tags table
#' @importFrom magrittr %>%
build_cohort_tbl_by_tag <- function(samples, dataset, tag){
    iatlas.api.client::query_cohort_selector(dataset, tag, samples = samples) %>%
        dplyr::select(
            "name" = "display",
            "group" = "name",
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
build_dm_cohort_object <- function(dataset, samples, mutation_id, mutation_tbl){

    sample_tbl <-
        iatlas.api.client::query_samples_by_mutation_status(
            ids = mutation_id, samples = samples
        ) %>%
        dplyr::select("sample", "group" = "status")

    mutation <- mutation_tbl %>%
        dplyr::filter(.data$id == mutation_id) %>%
        dplyr::pull(mutation) %>%
        unique()

    group_tbl <- create_dm_cohort_group_tbl(sample_tbl, mutation) %>%
        add_plot_colors_to_tbl()

    list(
        "sample_tbl"  = sample_tbl,
        "group_type"  = "custom",
        "group_tbl"   = group_tbl,
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
create_dm_cohort_group_tbl <- function(sample_tbl, mutation){
    sample_tbl %>%
        dplyr::group_by(.data$group) %>%
        dplyr::summarise(size = dplyr::n()) %>%
        dplyr::ungroup() %>%
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
    bin_number,
    feature_tbl
){
    sample_tbl <- build_feature_bin_sample_tbl(
        dataset,
        samples,
        feature_name,
        bin_number
    )

    feature_display <- feature_tbl %>%
        dplyr::filter(.data$name == feature_name) %>%
        dplyr::pull("display") %>%
        unique()

    group_tbl <- build_feature_bin_group_tbl(sample_tbl, feature_display) %>%
        add_plot_colors_to_tbl()

    list(
        "sample_tbl"  = sample_tbl,
        "group_type"  = "custom",
        "group_tbl"   = group_tbl,
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
    iatlas.api.client::query_feature_values(features = feature_name, datasets = dataset) %>%
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

