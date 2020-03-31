#' Build Immunomodulators Tibble
#'
#' #' @importFrom magrittr %>%
build_im_tbl <- function(){
    create_build_im_tbl_query() %>%
        perform_query("Build Immunomodulators Tibble")
}

#' Create Build Immunomodulators Table Query
create_build_im_tbl_query <- function(){
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


# TODO: Too slow to use
#' Immunomodulators Tibble
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
#         # numeric_values_to_query_list(1:200),
#         # "SELECT DISTINCT gene_id FROM genes_to_types",
#         ")"
#     ) %>%
#         perform_query()
#
#
#     paste0(
#         "SELECT DISTINCT gene_id FROM genes_to_samples ",
#         "WHERE sample_id IN (",
#         numeric_values_to_query_list(1:10000),
#         ") ",
#         "AND gene_id IN ",
#         "(SELECT gene_id FROM genes_to_types WHERE type_id IN ",
#         "(SELECT id FROM gene_types WHERE name = 'immunomodulator')",
#         ")"
#     ) %>%
#         perform_query()
#
#     query <- paste0(query, " ORDER BY hgnc")
#     perform_query(query, "Build Gene Tibble")
# }



#' Create Immunomodulators Gene List
#'
#' @param tbl A tibble with columns hgnc, id, and the variable group
#' @param group A column in the tibble
#' @importFrom magrittr %>%
#' @importFrom dplyr select
create_im_gene_list <- function(tbl, group){
    tbl %>%
        dplyr::select(
            class   = tidyselect::all_of(group),
            display = "hgnc",
            feature = "id"
        ) %>%
        create_nested_named_list()
}

#' Build Immunomodulators Distributions Plot Tibble
#'
#' @param gene_id An integer in the gene_id column of the genes_types table
#' @param sample_tbl A tibble with columns sample, group
#' @param scale_method A string, that is a scaling method
#' @importFrom magrittr %>%
#' @importFrom dplyr select inner_join
#' @importFrom rlang .data
build_im_distplot_tbl <- function(gene_id, sample_tbl, scale_method){
    gene_id %>%
        as.integer() %>%
        build_gene_expression_tbl_by_gene_ids() %>%
        dplyr::inner_join(sample_tbl, by = "sample_id") %>%
        dplyr::select(.data$group, value = .data$rna_seq_expr) %>%
        scale_tbl_value_column(scale_method) %>%
        dplyr::select(x = .data$group, y = .data$value)
}

#' Build Immunomodulators Datatable Tibble
#'
#' @param tbl A tibble
#' @importFrom magrittr %>%
#' @importFrom dplyr select mutate
#' @importFrom rlang .data
#' @importFrom stringr str_remove_all
build_im_dt_tbl <- function(tbl){
    tbl %>%
        dplyr::mutate(
            references = stringr::str_remove_all(.data$references, "[{}]")
        ) %>%
        dplyr::select(
            Hugo                  = .data$hgnc,
            `Entrez ID`           = .data$entrez,
            `Friendly Name`       = .data$friendly_name,
            `Gene Family`         = .data$gene_family,
            `Super Category`      = .data$super_category,
            `Immune Checkpoint`   = .data$immune_checkpoint,
            Function              = .data$gene_function,
            `Reference(s) [PMID]` = .data$references
        )
}
