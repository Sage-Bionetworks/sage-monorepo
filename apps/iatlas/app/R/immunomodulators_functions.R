#' Create Immunomodulators Gene List
#'
#' @param tbl A tibble with columns hgnc, entrez, and the variable group
#' @param group A column in the tibble
#' @importFrom magrittr %>%
#' @importFrom dplyr select
create_im_gene_list <- function(tbl, group){
    tbl %>%
        dplyr::select(
            class   = tidyselect::all_of(group),
            display = "hgnc",
            feature = "entrez"
        ) %>%
        iatlas.app::create_nested_named_list(.)
}

#' Build Immunomodulators Distributions Plot Tibble
#'
#' @param gene_id An integer in the gene_id column of the genes_types table
#' @param sample_tbl A tibble with columns sample, group
#' @param scale_method A string, that is a scaling method
#' @importFrom magrittr %>%
#' @importFrom dplyr select inner_join
#' @importFrom rlang .data
build_im_distplot_tbl <- function(gene, sample_tbl, scale_method){
    gene %>%
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
