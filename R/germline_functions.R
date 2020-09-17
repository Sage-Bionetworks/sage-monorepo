#' Add FDR annotation for plot display
#'
#' @param tbl A tibble
#' @param FDR A column with FDR values
#' @param intervals A vector with numbers for each FDR interval
#' @param symbols A vector of strings, which will be used as symbols for each FDR interval
#' @importFrom magrittr %>%
#' @importFrom rlang .data
#' @importFrom tidyr unite
#' @importFrom tidyselect all_of
create_FDR_label <- function(
  tbl,
  FDR,
  intervals,
  symbols
){


  #TODO:still duplicating all rows!
  #example: test_fdr <- create_FDR_label(heritability$EUR, "FDR", c(0.001, 0.05), c("***", "*"))

  intervals <-sort(intervals) #making sure that they are in an ascending order

  tbl$FDR_label <- ""

  df <- purrr::map_dfr(seq_along(intervals), function(i){

    tbl %>%
      dplyr::mutate(FDR_label =  replace(
          FDR_label, (FDR_label == "" & FDR <= intervals[i]), symbols[i]
        )
    )
    # print(dfm)
    # dfm
  })
}



#' Build Manhattan plot tibble
#'
#' @param gwas_df A Tibble with columns sample_id and group
#' @param chr_selected An integer in the id column of the samples table
#' @param bp_min An integer in the id column of the samples table
#' @param bp_max An integer in the id column of the samples table
#' @param feature_selected An integer in the id column of the samples table
#' @importFrom magrittr %>%
#' @importFrom dplyr left_join filter group_by summarise mutate
#' @importFrom rlang .data
build_manhattanplot_tbl <- function(
  gwas_df,
  chr_selected,
  bp_min,
  bp_max,
  feature_selected) {

  df <- gwas_df %>%

    # Compute chromosome size
    dplyr::filter(chr_col %in% chr_selected) %>%
    # dplyr::filter(bp_col >= bp_min & bp_col <= bp_max) %>%
    dplyr::group_by(chr_col) %>%
    dplyr::summarise(chr_len=max(bp_col)) %>%
    # Calculate cumulative position of each chromosome
    dplyr::mutate(tot=cumsum(as.numeric(chr_len))-chr_len) %>%
    dplyr::select(-chr_len) %>%
    # Add this info to the initial dataset
    dplyr::left_join(gwas_df, ., by = "chr_col") %>%
    # Add a cumulative position of each SNP
    dplyr::arrange(chr_col, bp_col) %>%
    dplyr::mutate( BPcum=bp_col+tot) %>%
    # Add highlight and annotation information
    dplyr::mutate( log10p = -log10(PLINK.P),
                   is_highlight=ifelse(display %in% feature_selected, "yes", "no"),
                   text = paste(display,"\nSNP name: ", snp_id, "\nSNP: ", snp_col, "\nPosition: ", bp_col, "\nChromosome: ", chr_col, sep="")) #%>%

  # Filter SNP to make the plot lighter
  #filter(-log10(P)>0.5)

  df

}

