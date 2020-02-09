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

#' Create Build Immunomodulators Table Query
create_build_immunomodulators_tbl_query <- function(){
    paste0(
        "SELECT a.id, a.hgnc, a.entrez, a.friendly_name, a.references, ",
        "gf.name AS gene_family, sc.name as super_category, ic.name AS ",
        "immune_checkpoint, gfunc.name as gene_function FROM (",
        create_get_genes_by_type_query("immunomodulator"), ") a ",
        "LEFT JOIN gene_families gf ON a.gene_family_id = gf.id ",
        "LEFT JOIN super_categories sc ON a.super_cat_id = sc.id ",
        "LEFT JOIN immune_checkpoints ic ON a.immune_checkpoint_id = ic.id ",
        "LEFT JOIN gene_functions gfunc ON a.gene_function_id = gfunc.id"
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

#' Create Feature Value Query from class ids
#' @param class_ids class ids in the features to samples table
create_feature_value_query_from_class_ids <- function(class_ids){
    subquery <- create_translate_values_query(
        "features",
        "id",
        "class_id",
        numeric_values_to_query_list(class_ids)
    )
    create_feature_value_query_from_subquery(subquery)
}

#' Create Feature Value Query from ids
#' @param ids ids in the features to samples table
create_feature_value_query_from_ids <- function(ids){
    create_feature_value_query_from_subquery(numeric_values_to_query_list(ids))
}

#' Create Feature Value Query from subquery
#' @param subquery subquery that results in a list of feature ids
create_feature_value_query_from_subquery <- function(subquery){
    paste0(
        "SELECT sample_id, feature_id, value ",
        "FROM features_to_samples ",
        "WHERE feature_id IN (",
        subquery,
        ")"
    )
}

#' Create Get Feature Display from Id Query
#' @param id A feature id
create_get_feature_display_from_id_query <- function(id){
    create_translate_values_query(
        "features",
        "display",
        "id",
        numeric_values_to_query_list(id)
    )
}

#' Create Get Class Id from Name Query
#' @param name A feature's name
create_get_class_id_from_name_query <- function(name){
    create_translate_values_query(
        "classes",
        "id",
        "name",
        string_values_to_query_list(name)
    )
}

#' Create Get Feature Id from Display Query
#' @param display A feature display name
create_get_feature_id_from_display_query <- function(display){
    create_translate_values_query(
        "features",
        "id",
        "display",
        string_values_to_query_list(display)
    )
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

#' Create Get Gene by Type Query
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

#' Create Translate Values Query
#' @param table The name of the table in the database to query
#' @param into The column in the table to translate into
#' @param from The column in the table to translate from
#' @param query A string that is a valid sql query that results in a one column
#' table that contains the from value
create_translate_values_query <- function(
    table,
    into,
    from,
    query
){
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
