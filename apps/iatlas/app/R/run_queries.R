# This is to stop the warning: no visible binding for global variable '.'
# when doing devtools::check()
if (getRversion() >= "2.15.1")  utils::globalVariables(c("."))

#' Build Sample Name Table
#'
#' @importFrom magrittr %>%
build_sample_name_tbl <- function(){
    "SELECT id AS sample_id, name AS sample_name FROM samples" %>%
        perform_query("Build Sample Name Table")
}

#' Build Feature Value Tibble from Feature IDs
#'
#' @param feature_ids Integers in the id column of the features_to_samples table
#' @importFrom magrittr %>%
build_feature_value_tbl_from_ids <- function(feature_ids){
    feature_ids %>%
        numeric_values_to_query_list() %>%
        create_feature_value_query() %>%
        perform_query("Build Feature Value Tibble from Feature IDs")
}

#' Build Feature Value Tibble from Class IDs
#'
#' @param class_ids An integer in the class_id column of the features table
#' @importFrom magrittr %>%
build_feature_value_tbl_from_class_ids <- function(class_ids){
    paste0(
        "SELECT fts.feature_id, fts.sample_id, fts.value, f.unit, ",
        "f.display AS feature, f.order ",
        "FROM features_to_samples fts ",
        "INNER JOIN features f ",
        "ON fts.feature_id = f.id ",
        "WHERE f.class_id IN (",
        numeric_values_to_query_list(class_ids),
        ")"
    ) %>%
        perform_query("Build Feature Value Tibble from Class IDs")
}

#' Build Feature Tibble
#'
#' @param sample_ids Integers in the sample_id column of the
#' features_to_samples table
#' @param class_ids Integers in the id column of the classes table
build_feature_tbl <- function(class_ids = NA, sample_ids = NA){
    query <- paste0(
        "SELECT ",
        create_id_to_class_subquery(),
        ", a.display, a.id, a.class_id FROM features a "
    )
    if (any(length(class_ids) > 1, !is.na(class_ids))) {
        query <- paste0(
            query,
            " WHERE a.class_id IN (",
            numeric_values_to_query_list(class_ids),
            ")"
        )
    }
    if (any(length(sample_ids) > 1, !is.na(sample_ids))) {
        query <- paste0(
            query,
            " WHERE a.id IN ",
            "(SELECT feature_id FROM features_to_samples ",
            "WHERE sample_id IN (", numeric_values_to_query_list(sample_ids),
            ")",
            "AND value IS NOT NULL",
            ")"
        )
    }
    query <- paste0(query, " ORDER BY class")
    perform_query(query, "Build Feature Tibble")
}


# TODO: Speed this query up
#' Build Gene Tibble
#'
#' @param sample_ids Integers in the sample_id column of the
#' genes_to_samples table
# build_gene_tbl <- function(sample_ids = NA){
#     query <- paste0(
#         "SELECT id, hgnc, entrez, description, io_landscape_name, ",
#         "a.references, friendly_name, ",
#         create_id_to_gene_family_subquery(),
#         ", ",
#         create_id_to_gene_function_subquery(),
#         ", ",
#         create_id_to_immune_checkpoint_subquery(),
#         ", ",
#         create_id_to_pathway_subquery(),
#         ", ",
#         create_id_to_super_category_subquery(),
#         ", ",
#         create_id_to_therapy_type_subquery(),
#         " from genes a "
#     )
#
#     if (any(length(sample_ids) > 1, !is.na(sample_ids))) {
#         query <- paste0(
#             query,
#             "WHERE a.id IN ",
#             "(SELECT gene_id FROM genes_to_samples ",
#             "WHERE sample_id IN (",
#             numeric_values_to_query_list(sample_ids),
#             ") AND rna_seq_expr IS NOT NULL)"
#         )
#     }
#     paste0(
#     "SELECT * FROM ",
#     "(SELECT DISTINCT gene_id FROM genes_to_samples ",
#     "WHERE sample_id IN (",
#     numeric_values_to_query_list(1:100000),
#     ") AND rna_seq_expr IS NOT NULL) a"
#     ) %>%
#         perform_query()
#
#     paste0(
#         "SELECT DISTINCT gene_id FROM genes_to_samples ",
#         "WHERE sample_id IN (",
#         numeric_values_to_query_list(1:10000),
#         ") ",
#         "AND gene_id IN (",
#         numeric_values_to_query_list(1:2000),
#         # "SELECT DISTINCT gene_id FROM genes_to_types",
#         ")"
#     ) %>%
#         perform_query()
#
#     query <- paste0(query, " ORDER BY hgnc")
#     perform_query(query, "Build Gene Tibble")
# }



#' Create Class List
#'
#' @importFrom magrittr %>%
#' @importFrom tibble deframe
create_class_list <- function(){
    "SELECT name, id FROM classes" %>%
        perform_query("Create Class List") %>%
        tibble::deframe(.)
}

#' Build Gene Expression Tibble by Gene IDs
#'
#' @param gene_ids Integers in the gene_id column of genes_to_samples
#' @importFrom magrittr %>%
build_gene_expression_tbl_by_gene_ids <- function(gene_ids){
    gene_ids %>%
        create_build_get_gene_expression_tbl_by_gene_ids_query() %>%
        perform_query("Build Gene Expression Tibble by Gene IDs")
}

#' Get Sample IDs from Dataset
#'
#' @param dataset The name of a dataset in the database
#' @importFrom magrittr %>%
#' @importFrom dplyr pull
#' @importFrom rlang .data
get_sample_ids_from_dataset <- function(dataset){
    paste0(
        "SELECT sample_id FROM samples_to_tags WHERE tag_id IN ",
        "(SELECT id FROM tags where name = '", dataset, "')"
    ) %>%
        perform_query("Get Sample IDs from Dataset") %>%
        dplyr::pull(.data$sample_id)
}



# Translation utilities -------------------------------------------------------
# The function take a single value from one or more columns and translate those
# into a value from a different column in the row

translate_value <- function(value, value_type, table, into, from){
    assertthat::assert_that(length(value) == 1)
    if (value_type == "id") {
        assertthat::assert_that(is.integer(value), value > 0)
        value_query <- as.character(value)
    } else if (value_type == "character") {
        assertthat::assert_that(is.character(value))
        value_query <- paste0("'", value, "'")
    } else {
        stop("Value of unallowed type.")
    }
    result <-
        create_translate_values_query(table, into, from, value_query) %>%
        perform_query("Translate Value") %>%
        dplyr::pull(into)
    assertthat::assert_that(length(result) == 1)
    return(result)
}

get_tag_display_from_id <- purrr::partial(
    translate_value,
    value_type = "id",
    table      = "tags",
    into       = "display",
    from       = "id"
)

get_tag_display_from_name <- purrr::partial(
    translate_value,
    value_type = "character",
    table      = "tags",
    into       = "display",
    from       = "name"
)

get_gene_hgnc_from_id <- purrr::partial(
    translate_value,
    value_type = "id",
    table      = "genes",
    into       = "hgnc",
    from       = "id"
)

get_gene_id_from_hgnc <- purrr::partial(
    translate_value,
    value_type = "character",
    table      = "genes",
    into       = "id",
    from       = "hgnc"
)

get_class_id_from_name <- purrr::partial(
    translate_value,
    value_type = "character",
    table      = "classes",
    into       = "id",
    from       = "name"
)

get_feature_display_from_id <- purrr::partial(
    translate_value,
    value_type = "id",
    table      = "features",
    into       = "display",
    from       = "id"
)

get_feature_name_from_id <- purrr::partial(
    translate_value,
    value_type = "id",
    table      = "features",
    into       = "name",
    from       = "id"
)

get_feature_id_from_display <- purrr::partial(
    translate_value,
    value_type = "character",
    table      = "features",
    into       = "id",
    from       = "display"
)

get_mutation_code_from_id <- purrr::partial(
    translate_value,
    value_type = "id",
    table      = "mutation_codes",
    into       = "code",
    from       = "id"
)

get_id_from_mutation_code <- purrr::partial(
    translate_value,
    value_type = "character",
    table      = "mutation_codes",
    into       = "id",
    from       = "code"
)

