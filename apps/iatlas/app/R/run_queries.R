# This is to stop the warning: no visible binding for global variable '.'
# when doing devtools::check()
if (getRversion() >= "2.15.1")  utils::globalVariables(c("."))





#' Get Feature Values Tibble From IDs
#'
#' @param ids An Integers in the id column of the features_to_samples table
#' @importFrom magrittr %>%
get_feature_values_from_ids <- function(ids){
    ids %>%
        create_feature_value_query_from_ids() %>%
        perform_query("Get feature values from ids")
}



#' Create Class List
#'
#' @importFrom magrittr %>%
#' @importFrom tibble deframe
create_class_list <- function(){
    "SELECT name, id FROM classes" %>%
        perform_query("get class id") %>%
        tibble::deframe(.)
}

#' Build Immunomodulators Table
#'
#' #' @importFrom magrittr %>%
build_immunomodultors_tbl <- function(){
    create_build_immunomodulators_tbl_query() %>%
        perform_query("Build Immunomodultors Table")
}

#' Build Gene Expression Table by Gene IDs
#'
#' @param gene_ids Integers in the gene_id column of genes_to_samples
#' @importFrom magrittr %>%
build_gene_expression_tbl_by_gene_ids <- function(gene_ids){
    gene_ids %>%
        create_build_get_gene_expression_tbl_by_gene_ids_query() %>%
        perform_query("Build gene expression table")
}

#' Get Sample IDs from Dataset
#'
#' @param dataset The name of a dataset in the database
#' @importFrom magrittr %>%
#' @importFrom dplyr pull
#' @importFrom rlang .data
get_sample_ids_from_dataset <- function(dataset){
    if (dataset == "TCGA") {
        tag_display <- "TCGA Study"
    } else if (dataset == "PCAWG") {
        tag_display <- "PCAWG Study"
    } else {
        tag_display = dataset
    }

    tag_display %>%
        create_get_sample_ids_from_parent_tag_display_query() %>%
        perform_query("Get Dataset Sample Ids") %>%
        dplyr::pull(.data$sample_id)
}







#' Build Cohort Table By Group
#'
#' @param sample_ids Integers in the id column of the samples table
#' @param group A String that is the display column of the tags table
#' @importFrom magrittr %>%
build_cohort_tbl_by_group <- function(sample_ids, group){
    paste0(
        "SELECT sts.sample_id, g.name AS group, g.display AS name, ",
        "g.characteristics, g.color FROM (",
        create_parent_group_query_from_display(group),
        ") g INNER JOIN samples_to_tags sts ON g.id = sts.tag_id ",
        "WHERE sample_id IN (",
        numeric_values_to_query_list(sample_ids),
        ")"
    ) %>%
        perform_query("Build Cohort Table")
}

#' Build Cohort Table By Feature ID
#'
#' @param sample_ids Integers in the id column of the samples table
#' @param feature_id An integer in the id column of the features_to_samples
#' table
#' @importFrom magrittr %>%
build_cohort_tbl_by_feature_id <- function(sample_ids, feature_id){
    paste(
        "SELECT a.sample_id, a.value FROM (",
        create_feature_value_query_from_ids(feature_id),
        ") a WHERE a.sample_id IN (",
        numeric_values_to_query_list(sample_ids),
        ")"
    ) %>%
        perform_query("Build immune feature bin table")
}

#' Build Feature Value Table
#'
#' @param feature_id An integer in the feature_id column of the
#' features_to_samples table
#' @importFrom magrittr %>%
build_feature_value_tbl <- function(feature_id){
    create_feature_value_query_from_ids(feature_id) %>%
        perform_query("Build feature value table")
}

# Translation utilities -------------------------------------------------------
# The function take a single value from one or more columns and translate those
# into a value from a different column in the row


#' Get HGNC Symbol from Gene ID
#'
#' @param id A integer from the id column of the genes table
#' @importFrom magrittr %>%
#' @importFrom dplyr pull
#' @importFrom rlang .data
get_gene_hgnc_from_id <- function(id){
    assertthat::assert_that(length(id) == 1, is.integer(id), id > 0)
    hgnc <-
        paste0("SELECT hgnc FROM genes WHERE id = ", id) %>%
        perform_query("Get gene HGNC") %>%
        dplyr::pull(.data$hgnc)
    assertthat::assert_that(length(hgnc) == 1)
    return(hgnc)
}

#' Get Gene ID from HGNC Symbol
#'
#' @param hgnc A string in the hgnc column of the genes table
#' @importFrom magrittr %>%
#' @importFrom dplyr pull
#' @importFrom rlang .data
get_gene_id_from_hgnc <- function(hgnc){
    assertthat::assert_that(length(hgnc) == 1, is.character(hgnc))
    id <-
        paste0("SELECT id FROM genes WHERE hgnc = '", hgnc, "'" ) %>%
        perform_query("get gene id") %>%
        dplyr::pull(.data$id)
    assertthat::assert_that(length(id) == 1)
    return(id)
}

#' Get Class ID from Class Name
#'
#' @param name An string in the name column of the classes table
#' @importFrom magrittr %>%
#' @importFrom dplyr pull
#' @importFrom rlang .data
get_class_id_from_name <- function(name){
    assertthat::assert_that(length(name) == 1, is.character(name))
    id <-
        paste0("SELECT id FROM classes WHERE name = '", name, "'") %>%
        perform_query("Get class id from name") %>%
        dplyr::pull(.data$id)
    assertthat::assert_that(length(id) == 1)
    return(id)
}

#' Get Feature Display Name From ID
#'
#' @param id An integer in the id column of the features table
#' @importFrom magrittr %>%
#' @importFrom dplyr pull
#' @importFrom rlang .data
get_feature_display_from_id <- function(id){
    assertthat::assert_that(length(id) == 1, is.integer(id), id > 0)
    display <-
        paste0("SELECT display FROM features WHERE id = ", id) %>%
        perform_query("Get feature display from id") %>%
        dplyr::pull(.data$display)
    assertthat::assert_that(length(display) == 1)
    return(display)
}

#' Get Feature ID From Display Name
#'
#' @param display A string in the display column of the features table
#' @importFrom magrittr %>%
#' @importFrom dplyr pull
#' @importFrom rlang .data
get_feature_id_from_display <- function(display){
    assertthat::assert_that(length(display) == 1, is.character(display))
    id <-
        paste0("SELECT id FROM features WHERE display = '", display , "'") %>%
        perform_query("Get feature id from display") %>%
        dplyr::pull(.data$id)
    assertthat::assert_that(length(id) == 1)
    return(id)
}





# REDO after refactoring of mutations in database -----------------------------

# create_driver_mutation_list <- function(){
#     paste(
#         "SELECT hgnc, id FROM (",
#         create_get_genes_by_type_query("driver_mutation"),
#         ") a"
#     ) %>%
#         perform_query("Get mutation genes") %>%
#         tibble::deframe()
# }


#' Build Driver Results Table
#'
#' @importFrom magrittr %>%
# build_driver_results_tbl <- function(group_name, feature_id, min_wt, min_mut){
#     subquery1 <- paste0(
#         "SELECT id from tags WHERE display = '",
#         group_name,
#         "'"
#     )
#
#     subquery2 <- paste(
#         "SELECT tag_id from tags_to_tags WHERE related_tag_id IN (",
#         subquery1,
#         ")"
#     )
#
#     subquery3 <- paste(
#         "SELECT p_value, fold_change, log10_p_value,",
#         "log10_fold_change, gene_id, tag_id",
#         "FROM driver_results",
#         "WHERE feature_id = ", feature_id,
#         "AND tag_id IN (", subquery2, ")",
#         "AND n_wt >= ", min_wt,
#         "AND n_mut >= ", min_mut
#     )
#
#     paste(
#         "SELECT a.p_value, a.fold_change, a.log10_p_value,",
#         "a.log10_fold_change, g.gene, g.gene_id, t.group, t.tag_id FROM",
#         "(", subquery3, ") a",
#         "LEFT OUTER JOIN (SELECT id AS gene_id, hgnc AS gene FROM genes) g",
#         "ON a.gene_id = g.gene_id",
#         "LEFT OUTER JOIN (SELECT id AS tag_id, name As group FROM tags) t",
#         "ON a.tag_id = t.tag_id"
#     ) %>%
#         perform_query("Build Driver Results Table")
# }







