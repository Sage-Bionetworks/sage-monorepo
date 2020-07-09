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
