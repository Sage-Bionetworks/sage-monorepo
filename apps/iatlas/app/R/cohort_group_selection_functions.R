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
        tibble::deframe(.)
}

build_cohort_mutation_tbl <- function(){
    iatlas.app::query_mutations(type = "driver_mutation") %>%
        dplyr::mutate(
            "mutation" = stringr::str_c(.data$hgnc, ":", .data$code)
        )
}

# TODO: Fix filter object
# TODO: Fix driver mutation cohort builder
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
    driver_mutation = NULL,
    mutation_tbl = NULL,
    feature_bin_name = NULL,
    feature_bin_number = NULL,
    feature_bin_tbl = NULL
){
    samples <- filter_obj$samples
    if (group_choice %in% c(
        "Immune_Subtype", "TCGA_Subtype", "TCGA_Study", "PCAWG_Study")
    ) {
        cohort_object <- build_tag_cohort_object(
            samples, dataset, group_choice
        )
        cohort_object$feature_tbl <-
            query_features_by_class(dataset, group_choice)
    } else if (group_choice == "Driver Mutation") {
        cohort_object <- build_dm_cohort_object(
            samples, driver_mutation, mutation_tbl
        )
        cohort_object$feature_tbl <-
            query_features_by_class(dataset)
    } else if (group_choice == "Immune Feature Bins") {
        cohort_object <- build_feature_bin_cohort_object(
            dataset,
            samples,
            feature_bin_name,
            feature_bin_number,
            feature_bin_tbl
        )
        cohort_object$feature_tbl <-
            query_features_by_class(dataset)
    }
    cohort_object$dataset     <- dataset
    cohort_object$filters     <- "None"
    # cohort_object$filters     <- filter_obj$filters
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
build_tag_cohort_object <- function(samples, dataset, tag){
    cohort_tbl  <- build_cohort_tbl_by_tag(samples, dataset, tag)
    sample_tbl   <- cohort_tbl %>%
        dplyr::select("sample", "group") %>%
        dplyr::arrange(.data$sample)
    group_tbl <- cohort_tbl %>%
        dplyr::select(-"sample") %>%
        dplyr::distinct() %>%
        dplyr::arrange(.data$group)
    colors_list <- group_tbl %>%
        dplyr::select(.data$group, .data$color) %>%
        dplyr::distinct() %>%
        create_plot_colors_list(.)
    list(
        "sample_tbl"  = sample_tbl,
        "group_type"  = "tag",
        "group_tbl"   = group_tbl,
        "group_name"  = tag,
        "plot_colors" = colors_list
    )
}

#' Build Cohort Tibble By Tag
#'
#' @param sample_ids Integers in the id column of the samples table
#' @param group A String that is the display column of the tags table
#' @importFrom magrittr %>%
build_cohort_tbl_by_tag <- function(samples, dataset, tag){
    query_cohort_selector(dataset, tag) %>%
        dplyr::select(
            "name" = "display",
            "group" = "name",
            "characteristics",
            "color",
            "sample",
            "size"
        ) %>%
        dplyr::filter(.data$sample %in% samples)
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
build_dm_cohort_object <- function(samples, mutation, mutation_tbl){
    # gene <- get_gene_from_mutation(mutation) %>%  print()
    # code <- get_code_from_mutation(mutation) %>%  print()
    print(mutation_tbl)
    # gene_id <- get_gene_id_from_hgnc(gene)
    # code_id <- get_id_from_mutation_code(code)
    # cohort_tbl  <- create_dm_cohort_sample_tbl(samples, gene_id, code_id)
    # colors_list <- cohort_tbl %>%
    #     dplyr::select(.data$group) %>%
    #     create_plot_colors_list()
    # list(
    #     "sample_tbl"  = cohort_tbl,
    #     "group_type"  = "custom",
    #     "group_tbl"   = create_dm_cohort_group_tbl(cohort_tbl, gene),
    #     "group_name"  = paste0(
    #         "Mutation Status", gene, code, sep = ": "
    #     ),
    #     "plot_colors" = colors_list
    # )
}

#' Get Gene From Mutation
#'
#' @param mutation A string
#' @importFrom stringr str_remove
get_gene_from_mutation <- function(mutation){
    stringr::str_remove(mutation, ":[:print:]+$")
}

#' Get Code From Mutation
#'
#' @param mutation A string
#' @importFrom stringr str_remove
get_code_from_mutation <- function(mutation){
    stringr::str_remove(mutation, "^[:print:]+:")
}

#' Create Driver Mutation Cohort Sample Tibble
#'
#' @param sample_ids A vecotr of itnegers
#' @param gene_id An integer
#' @param code_id An integer
create_dm_cohort_sample_tbl <- function(sample_ids, gene_id, code_id){
    paste0(
        "SELECT sample_id, status AS group ",
        "FROM samples_to_mutations WHERE mutation_id IN ",
        "(SELECT id FROM mutations a WHERE a.mutation_type_id IN ",
        "(SELECT id FROM mutation_types WHERE name = 'driver_mutation') ",
        "AND gene_id = ", gene_id, " ",
        "AND mutation_code_id = ", code_id, ")"
    ) %>%
        perform_query("Create Driver Mutation Sample Tibble")
}


#' Create Driver Mutation Cohort Group Tibble
#'
#' @param sample_tbl A tibble
#' @param gene_name A string
#' @importFrom magrittr %>%
#' @importFrom dplyr group_by summarise ungroup arrange n mutate
#' @importFrom rlang .data
create_dm_cohort_group_tbl <- function(sample_tbl, gene_name){
    sample_tbl %>%
        dplyr::group_by(.data$group) %>%
        dplyr::summarise(size = dplyr::n()) %>%
        dplyr::ungroup() %>%
        dplyr::mutate(name = gene_name, characteristics = "Mutation Status") %>%
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
    colors_list <- sample_tbl %>%
        dplyr::select(.data$group) %>%
        create_plot_colors_list()
    feature_display <- feature_tbl %>%
        dplyr::filter(.data$name == feature_name) %>%
        dplyr::pull("display") %>%
        unique()
    list(
        "sample_tbl"  = sample_tbl,
        "group_type"  = "custom",
        "group_tbl"   = build_feature_bin_group_tbl(sample_tbl, feature_display),
        "group_name"  = paste("Immune Feature Bins:", feature_display),
        "plot_colors" = colors_list
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
    query_feature_values(dataset, feature = feature_name) %>%
        dplyr::filter(.data$sample %in% samples) %>%
        dplyr::mutate("group" = as.character(cut(.data$value, n_bins))) %>%
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
            "characteristics" = "Immune feature bin range",
            "color" = NA
        ) %>%
        dplyr::arrange(.data$group)
}

# various ---------------------------------------------------------------------

#' Create Plot Colors List
#'
#' @param tbl A tibble with name group, and optionally color
#' @importFrom magrittr %>%
#' @importFrom dplyr distinct arrange n
#' @importFrom rlang .data
#' @importFrom tibble deframe
#' @importFrom viridisLite viridis
create_plot_colors_list <- function(tbl){
    tbl <- tbl %>%
        dplyr::distinct() %>%
        dplyr::arrange(.data$group)
    if (!"color" %in% colnames(tbl) || any(is.na(tbl$color))) {
        tbl <- dplyr::mutate(tbl, color = viridisLite::viridis(dplyr::n()))
    }
    tibble::deframe(tbl)
}
