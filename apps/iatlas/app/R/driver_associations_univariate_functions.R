#' Build Univariate Driver Results Tibble
#'
#' @param group_name A string in the name column of the tags table
#' @param feature_id An integer in the feature_id column of the driver_results
#' table
#' @param min_wt An integer
#' @param min_mut An integer
#' @importFrom dplyr mutate
#' @importFrom magrittr %>%
#' @importFrom rlang .data
build_ud_results_tbl <- function(tags, feature, min_wt, min_mut){
}

#' Build Univariate Driver Violin Tibble
#'
#' @param feature_id An integer in the features_to_samples table
#' @param gene_id An interger in the genes_samples_mutations table
#' @param tag_id An integer in the samples_to_tags table
#' @param mutation_id An integer in the genes_samples_mutations table
#' @importFrom dplyr select
#' @importFrom magrittr %>%
#' @importFrom rlang .data
build_ud_driver_violin_tbl <- function(feature, entrez, tag, mutation_code){

    # subquery1 <- paste(
    #     "SELECT sample_id FROM samples_to_tags",
    #     "WHERE tag_id = ",
    #     tag_id
    # )
    #
    # subquery2 <- paste0(
    #     "SELECT sample_id, value FROM features_to_samples ",
    #     "WHERE feature_id = ", feature_id, " ",
    #     "AND sample_id IN (", subquery1, ")"
    # )
    #
    # subquery3 <- paste0(
    #     "SELECT sample_id, status FROM samples_to_mutations ",
    #     "WHERE mutation_id IN ",
    #     "(SELECT id FROM mutations ",
    #     "WHERE gene_id  = ", gene_id, " ",
    #     "AND mutation_code_id = ", mutation_id, " ",
    #     "AND mutation_type_id IN ",
    #     "(SELECT id FROM mutation_types WHERE name = 'driver_mutation'))"
    # )
    #
    # paste(
    #     "SELECT f.value, g.status FROM",
    #     "(", subquery2, ") f",
    #     "INNER JOIN",
    #     "(", subquery3, ") g",
    #     "ON f.sample_id = g.sample_id"
    # ) %>%
    #     perform_query("build univariate driver violin table") %>%
    #     dplyr::select(x = .data$status, y = .data$value)
}

