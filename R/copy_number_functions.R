
build_cnv_group_list <- function(tbl){
  tbl %>%
    tibble::deframe(.) %>%
    c("All" = "All", .)
}

build_cnv_gene_list <- function(gene_set_tbl, gene_tbl){
  list(
    "All Genes" = "All",
    "Gene Sets" = tibble::deframe(gene_set_tbl),
    "Genes" = tibble::deframe(gene_tbl)
  )
}

get_cnv_entrez_query_from_filters <- function(filters, gene_set_tbl, gene_tbl){
  if ("All" %in% filters) return(gene_tbl$entrez)
  gene_sets <- filters %>%
    purrr::keep(., . %in% gene_set_tbl$name)
  if(length(gene_sets) == 0) return(as.integer(filters))
  genes <- filters %>%
    purrr::discard(., . %in% gene_sets)
  iatlas.api.client::query_genes_by_gene_types(gene_sets) %>%
    dplyr::pull("entrez") %>%
    unique() %>%
    union(genes) %>%
    as.integer()
}

create_cnv_results_string <- function(result_tbl){
    n_genes <- result_tbl %>%
        dplyr::pull(.data$hgnc) %>%
        unique() %>%
        sort() %>%
        length()
    paste0(
        "Total number of rows: ", nrow(result_tbl),
        ", Number of genes: ", n_genes
    )
}

build_cnv_dt_tbl <- function(tbl){
  tbl %>%
    dplyr::mutate(mean_diff = .data$mean_normal - .data$mean_cnv) %>%
    dplyr::select(
      Metric             = .data$feature_display,
      Group              = .data$tag_short_display,
      Gene               = .data$hgnc,
      Direction          = .data$direction,
      `Mean Normal`      = .data$mean_normal,
      `Mean CNV`         = .data$mean_cnv,
      `Mean Diff`        = .data$mean_diff,
      `T stat`           = .data$t_stat,
      `Neg log10 pvalue` = .data$log10_p_value
    ) %>%
    dplyr::mutate_if(is.numeric, round, 3)
}








