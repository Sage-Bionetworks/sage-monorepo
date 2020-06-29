#' Get Cohort Available Groups
#'
#' @param tbl A tibble
#' @param .dataset A string
#' @importFrom magrittr %>%
#' @importFrom dplyr select filter
get_cohort_available_groups <- function(tbl, .dataset){
    tbl %>%
        dplyr::filter(.data$dataset == .dataset) %>%
        dplyr::select("group", "group_internal") %>%
        tibble::deframe(.)
}

#' Build Driver Mutation Tibble
#' @importFrom magrittr %>%
#' @importFrom dplyr mutate
#' @importFrom rlang .data
build_dm_tbl <- function(){
    paste0(
        "SELECT ",
        create_id_to_hgnc_subquery(),
        ", ",
        create_id_to_mutation_code_subquery(),
        " FROM mutations a WHERE a.mutation_type_id IN ",
        "(SELECT id FROM mutation_types WHERE name = 'driver_mutation')"
    ) %>%
        perform_query() %>%
        dplyr::mutate(mutation = paste0(.data$gene, ":", .data$mutation_code))
}

#' Create Cohort Object
#'
#' @param filter_obj A named list with element sample_ids and filters
#' @param group_choice A string
#' @param dataset A string
#' @param driver_mutation A string
#' @param immune_feature_bin_id An integer
#' @param immune_feature_bin_number An integer
#' @importFrom purrr list_modify
create_cohort_object <- function(
    filter_obj,
    group_choice,
    dataset,
    driver_mutation = NULL,
    immune_feature_bin_id = NULL,
    immune_feature_bin_number = NULL
){
    sample_ids <- filter_obj$sample_ids
    if (group_choice %in% c("Immune_Subtype", "TCGA_Subtype", "TCGA_Study")) {
        cohort_object <- create_tag_cohort_object(sample_ids, dataset, group_choice)
    } else if (group_choice == "Driver_Mutation") {
        cohort_object <- create_dm_cohort_object(sample_ids, driver_mutation)
    } else if (group_choice == "Immune_Feature_Bins") {
        cohort_object <- create_feature_bin_cohort_object(
            sample_ids,
            immune_feature_bin_id,
            immune_feature_bin_number
        )
    }
    cohort_object$dataset     <- dataset
    cohort_object$filters     <- filter_obj$filters
    cohort_object$feature_tbl <- build_feature_tbl(sample_ids = sample_ids)
    return(cohort_object)
}

# tag choice ------------------------------------------------------------------

#' Create Tag Cohort Object
#'
#' @param sample_ids A vector of integers
#' @param group_choice A string
#' @importFrom magrittr %>%
#' @importFrom dplyr select
#' @importFrom rlang .data
create_tag_cohort_object <- function(sample_ids, dataset, tag){
    cohort_tbl  <- build_cohort_tbl_by_tag(sample_ids, dataset, tag)
    sample_tbl   <- cohort_tbl %>%
        dplyr::select("sample_id", "group") %>%
        dplyr::inner_join(
            "SELECT id AS sample_id, name AS sample_name FROM samples" %>%
                perform_query(),
            by = "sample_id"
        )
    group_tbl <- cohort_tbl %>%
        dplyr::select(-"sample_id") %>%
        dplyr::distinct()
    colors_list <- cohort_tbl %>%
        dplyr::select(.data$group, .data$color) %>%
        dplyr::distinct() %>%
        create_plot_colors_list()
    list(
        "sample_tbl"  = sample_tbl,
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
build_cohort_tbl_by_tag <- function(sample_ids, dataset, tag){
    iatlas.app::query_cohort_selector(dataset, tag) %>%
        tidyr::unnest(cols = c("sampleIds")) %>%
        dplyr::select(
            "color",
            "name" = "display",
            "characteristics",
            "sample_id" = "sampleIds",
            "group" = "name",
            "size" = "sampleCount"
        ) %>%
        dplyr::filter(.data$sample_id %in% c(sample_ids))
}

#' Create Tag Group Tibble
#'
#' @param cohort_tbl A Tibble with columns group, name, characteristics
#' @importFrom magrittr %>%
#' @importFrom dplyr group_by summarise ungroup arrange n
#' @importFrom rlang .data
create_tag_group_tbl <- function(cohort_tbl){
    cohort_tbl %>%
        dplyr::group_by(.data$group, .data$name, .data$characteristics) %>%
        dplyr::summarise(size = dplyr::n()) %>%
        dplyr::ungroup() %>%
        dplyr::arrange(.data$group)
}

# mutation choice -------------------------------------------------------------

#' Create Driver Mutation Cohort Object
#'
#' @param sample_ids A vector of integers
#' @param mutation A string
#' @importFrom magrittr %>%
#' @importFrom dplyr select
#' @importFrom rlang .data
create_dm_cohort_object <- function(sample_ids, mutation){
    gene <- get_gene_from_mutation(mutation)
    code <- get_code_from_mutation(mutation)
    gene_id <- get_gene_id_from_hgnc(gene)
    code_id <- get_id_from_mutation_code(code)
    cohort_tbl  <- create_dm_cohort_sample_tbl(sample_ids, gene_id, code_id)
    colors_list <- cohort_tbl %>%
        dplyr::select(.data$group) %>%
        create_plot_colors_list()
    list(
        "sample_tbl"  = cohort_tbl,
        "group_tbl"   = create_dm_cohort_group_tbl(cohort_tbl, gene),
        "group_name"  = paste0(
            "Mutation Status", gene, code, sep = ": "
        ),
        "plot_colors" = colors_list
    )
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


#' Create Driver Mutation Cohort Group Tibbl
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

#' Create Feature Bin Cohort Object
#'
#' @param sample_ids A vector of integers
#' @param feature_id An integer
#' @param bin_number An integer
#' @importFrom magrittr %>%
#' @importFrom dplyr select
#' @importFrom rlang .data
create_feature_bin_cohort_object <- function(
    sample_ids,
    feature_id,
    bin_number
){
    feature_name <- get_feature_display_from_id(feature_id)
    sample_tbl   <- create_feature_bin_sample_tbl(
        sample_ids,
        feature_id,
        bin_number
    )
    colors_list <- sample_tbl %>%
        dplyr::select(.data$group) %>%
        create_plot_colors_list()
    list(
        "sample_tbl"  = sample_tbl,
        "group_tbl"   = create_feature_bin_group_tbl(sample_tbl, feature_name),
        "group_name"  = paste("Immune Feature Bins:", feature_name),
        "plot_colors" = colors_list
    )
}

#' Create Feature Bin Sample Tibble
#'
#' @param sample_ids A vector of integers
#' @param feature_id An integer
#' @param n_bins An integer
#' @importFrom magrittr %>%
#' @importFrom dplyr select mutate
#' @importFrom rlang .data
create_feature_bin_sample_tbl <- function(sample_ids, feature_id, n_bins){
    build_cohort_tbl_by_feature_id(sample_ids, feature_id) %>%
        dplyr::mutate(group = as.character(cut(.data$value, n_bins))) %>%
        dplyr::select(-.data$value)
}

#' Build Cohort Tibble By Feature ID
#'
#' @param sample_ids Integers in the id column of the samples table
#' @param feature_id An integer in the id column of the features_to_samples
#' table
#' @importFrom magrittr %>%
build_cohort_tbl_by_feature_id <- function(sample_ids, feature_id){
    paste(
        "SELECT a.sample_id, a.value FROM (",
        create_feature_value_query(feature_id),
        ") a WHERE a.sample_id IN (",
        numeric_values_to_query_list(sample_ids),
        ")"
    ) %>%
        perform_query("Build Cohort Tibble By Feature ID")
}

#' Create Feature Bin Group Tibble
#'
#' @param sample_tbl A tibble with name group
#' @param feature_name A string
#' @importFrom magrittr %>%
#' @importFrom dplyr group_by summarise ungroup arrange n mutate
#' @importFrom rlang .data
create_feature_bin_group_tbl <- function(sample_tbl, feature_name){
    sample_tbl %>%
        dplyr::group_by(.data$group) %>%
        dplyr::summarise(size = dplyr::n()) %>%
        dplyr::ungroup() %>%
        dplyr::mutate(
            name = feature_name,
            characteristics = "Immune feature bin range"
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
