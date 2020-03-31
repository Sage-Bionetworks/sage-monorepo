#' Create Build Get Gene Expression Table By Gene IDs Query
#'
#' @param gene_ids Integers in the gene_id column of the genes_to_samples table
create_build_get_gene_expression_tbl_by_gene_ids_query <- function(gene_ids){
    paste0(
        "SELECT gene_id, sample_id, rna_seq_expr FROM genes_to_samples WHERE ",
        "gene_id IN (",
        numeric_values_to_query_list(gene_ids),
        ")"
    )
}

#' Create Get Sample IDs From Parent Tag Display Query
#'
#' @param display A string in the display column of the tags table
create_get_sample_ids_from_parent_tag_display_query <- function(display){
    paste0(
        "SELECT sample_id FROM samples_to_tags WHERE tag_id IN (",
        "SELECT tag_id FROM tags_to_tags WHERE related_tag_id IN (",
        "SELECT id FROM tags WHERE display IN (",
        string_values_to_query_list(display),
        ")))"
    )
}

#' Create Combined Feature Values Query From Class Ids
#' @param class_ids class ids in the features to samples table
create_combined_feature_values_query_from_class_ids <- function(class_ids){
    subquery <- paste0(
        "SELECT * FROM ",
        "features WHERE class_id IN (",
        numeric_values_to_query_list(class_ids),
        ")"
    )
    paste0(
        "SELECT * FROM (SELECT * FROM features_to_samples) a ",
        "INNER JOIN (", subquery, ") b ",
        "ON a.feature_id = b.id"
    )
}

#' Create Feature Value Query
#' @param subquery One of :
#' - A subquery that results in a list of feature ids
#' - A subquery that results in a one column table of feature ids
#' - A single feature_id
create_feature_value_query <- function(subquery){
    paste0(
        "SELECT sample_id, feature_id, value ",
        "FROM features_to_samples ",
        "WHERE feature_id IN (",
        subquery,
        ") AND value IS NOT NULL"
    )
}

#' Create Parent Group Query From Id
#' @param id The id of the parent group
create_parent_group_query_from_id <- function(id) {
    tag_id_query <- create_translate_values_query(
        "tags_to_tags", "tag_id", "related_tag_id",
        numeric_values_to_query_list(id)
    )

    paste0(
        "SELECT * FROM tags WHERE id IN (",
        tag_id_query,
        ")"
    )
}

#' Create Get Genes by Type Query
#' @param gene_type The name of the gene type
create_get_genes_by_type_query <- function(gene_type){
    gene_types_subquery <- create_translate_values_query(
        "gene_types", "id", "name", string_values_to_query_list(gene_type)
    )
    gene_ids_subquery <- create_translate_values_query(
        "genes_to_types", "gene_id", "type_id", gene_types_subquery
    )
    paste0("SELECT * FROM genes WHERE id IN (", gene_ids_subquery, ")")
}

#' Create Parent Group Query From Display
#' @param display The display name of the parent group
create_parent_group_query_from_display <- function(display){
    parent_tag_query <- create_translate_values_query(
        "tags", "id", "display",
        string_values_to_query_list(display)
    )

    create_parent_group_query_from_id(parent_tag_query)
}

# helper functions ------------------------------------------------------------

#' Create Correlated Subquery
create_correlated_subquery <- function(table, into, from, value, new_column){
    paste0(
        "(SELECT ", into, " FROM ", table, " WHERE ", from, " = ", value,
        ") AS ", new_column
    )
}

create_id_to_pathway_subquery <- purrr::partial(
    create_correlated_subquery,
    table      = "pathways",
    into       = "name",
    from       = "id",
    value      = "a.pathway_id",
    new_column = "pathway"
)

create_id_to_therapy_subquery <- purrr::partial(
    create_correlated_subquery,
    table      = "therapy_types",
    into       = "name",
    from       = "id",
    value      = "a.therapy_type_id",
    new_column = "therapy"
)

create_id_to_mutation_code_subquery <- purrr::partial(
    create_correlated_subquery,
    table      = "mutation_codes",
    into       = "code",
    from       = "id",
    value      = "a.mutation_code_id",
    new_column = "mutation_code"
)

create_id_to_hgnc_subquery <- purrr::partial(
    create_correlated_subquery,
    table      = "genes",
    into       = "hgnc",
    from       = "id",
    value      = "a.gene_id",
    new_column = "gene"
)

create_id_to_gene_family_subquery <- purrr::partial(
    create_correlated_subquery,
    table      = "gene_families",
    into       = "name",
    from       = "gene_family_id",
    value      = "a.id",
    new_column = "gene_family"
)

create_id_to_gene_function_subquery <- purrr::partial(
    create_correlated_subquery,
    table      = "gene_families",
    into       = "name",
    from       = "gene_function_id",
    value      = "a.id",
    new_column = "gene_function"
)

create_id_to_immune_checkpoint_subquery <- purrr::partial(
    create_correlated_subquery,
    table      = "immune_checkpoints",
    into       = "name",
    from       = "immune_checkpoint_id",
    value      = "a.id",
    new_column = "immune_checkpoint"
)

create_id_to_pathway_subquery <- purrr::partial(
    create_correlated_subquery,
    table      = "pathways",
    into       = "name",
    from       = "pathway_id",
    value      = "a.id",
    new_column = "pathway"
)

create_id_to_super_category_subquery <- purrr::partial(
    create_correlated_subquery,
    table      = "super_categories",
    into       = "name",
    from       = "super_cat_id",
    value      = "a.id",
    new_column = "super_category"
)

create_id_to_therapy_type_subquery <- purrr::partial(
    create_correlated_subquery,
    table      = "therapy_types",
    into       = "name",
    from       = "therapy_type_id",
    value      = "a.id",
    new_column = "therapy_type"
)


create_id_to_class_subquery <- purrr::partial(
    create_correlated_subquery,
    table      = "classes",
    into       = "name",
    from       = "id",
    value      = "a.class_id",
    new_column = "class"
)

#' Create Translate Values Query
#' @param table The name of the table in the database to query
#' @param into The column in the table to translate into
#' @param from The column in the table to translate from
#' @param query A string that is a valid sql query that results in a one column
#' table that contains the from value
create_translate_values_query <- function(table, into, from, query){
    paste0(
        "SELECT ", into, " FROM ", table, " WHERE ", from, " IN (", query, ")"
    )
}

#' String Values to Query List
#' @param values A character vector
string_values_to_query_list <- function(values){
    paste0("'", values, "'", collapse = ", ")
}

#' Numeric Values to Query List
#' @param values A numeric vector
numeric_values_to_query_list <- function(values){
    paste0(values, collapse = ", ")
}



