
build_im_dt_tbl <- function(){
  ims <-
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
    dplyr::arrange(.data$hgnc)

  references <- ims %>%
    dplyr::select("entrez", "publications") %>%
    tidyr::unnest(cols = "publications") %>%
    dplyr::mutate("references" = stringr::str_c(
      "https://www.ncbi.nlm.nih.gov/pubmed/",
      .data$pubmedId,
      sep = "/"
    )) %>%
    dplyr::select(-"pubmedId") %>%
    dplyr::group_by(.data$entrez) %>%
    dplyr::summarise(
      "references" = stringr::str_c(.data$references, collapse = " | "),
      .groups = "drop"
    )

  ims %>%
    dplyr::left_join(references, by = "entrez") %>%
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
