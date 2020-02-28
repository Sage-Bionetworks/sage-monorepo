create_im_gene_list <- function(tbl, group){
    tbl %>%
        dplyr::select(
            class   = tidyselect::all_of(group),
            display = "hgnc",
            feature = "id"
        ) %>%
        create_nested_named_list()
}

build_im_distplot <- function(gene_id, sample_tbl, scale_method){
    gene_id %>%
        as.integer() %>%
        build_gene_expression_tbl_by_gene_ids() %>%
        dplyr::inner_join(sample_tbl, by = "sample_id") %>%
        dplyr::select(.data$group, value = .data$rna_seq_expr) %>%
        scale_tbl_value_column(scale_method) %>%
        dplyr::select(x = .data$group, y = .data$value)
}

build_im_target_dt_tbl <- function(tbl){
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
