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
        dplyr::arrange(.data$class) %>%
        create_nested_named_list(.)
}

get_im_hgnc_from_tbl <- function(tbl, .entrez){
    tbl %>%
        dplyr::filter(.data$entrez == .entrez) %>%
        dplyr::pull("hgnc")
}


#' Build Immunomodulators Distributions Plot Tibble
#'
#' @param gene_id An integer in the gene_id column of the genes_types table
#' @param sample_tbl A tibble with columns sample, group
#' @param scale_method A string, that is a scaling method
#' @importFrom magrittr %>%
#' @importFrom dplyr select inner_join
#' @importFrom rlang .data
build_im_distplot_tbl <- function(cohort_object, gene, scale_method){
    cohort_object %>%
        query_gene_expression_with_cohort_object(entrez = gene) %>%
        dplyr::inner_join(cohort_object$sample_tbl, by = "sample") %>%
        dplyr::select(.data$group, "value" = .data$rna_seq_expr) %>%
        scale_tbl_value_column(scale_method) %>%
        dplyr::select("x" = .data$group, "y" = .data$value)
}

#' Build Immunomodulators Datatable Tibble
#'
#' @param tbl A tibble
#' @importFrom magrittr %>%
#' @importFrom dplyr select mutate
#' @importFrom rlang .data
#' @importFrom stringr str_remove_all
build_im_dt_tbl <- function(){
    iatlas.api.client::query_immunomodulators() %>%
        dplyr::select(
            "hgnc",
            "entrez",
            "friendly_name",
            "gene_family",
            "super_category",
            "immune_checkpoint",
            "gene_function",
            "publications"
        ) %>%
        dplyr::arrange(.data$hgnc) %>%
        tidyr::unnest(cols = "publications", keep_empty = T) %>%
        dplyr::mutate("references" = stringr::str_c(
            "https://www.ncbi.nlm.nih.gov/pubmed/",
            .data$pubmedId,
            sep = " | "
        )) %>%
        dplyr::select(-"pubmedId") %>%
        dplyr::group_by_at(dplyr::vars(-.data$references)) %>%
        dplyr::mutate("references" = stringr::str_c(.data$references, sep = " ")) %>%
        dplyr::ungroup() %>%
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
