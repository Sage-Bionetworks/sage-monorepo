#Heritability functions

#' Prepare heritability tibble for plotting
#'
#' @param heritablity_data A tibble
#' @param parameter Parameter to be used for selection of results (eg, ancestry cluster, immune feature)
#' @param group Specific group, in the selected parameter, to be displayed (eg. European - ancestry cluster, NK cells - immune feature)
#' @param pval_thres Maximun p-value to be included
#' @importFrom magrittr %>%

create_heritability_df <- function(
  heritablity_data,
  parameter = "cluster",
  group = "European",
  pval_thres = 0.05,
  ancestry_labels
){

  ancestry_df <- names(ancestry_labels)
  names(ancestry_df) <- ancestry_labels

    df <- heritablity_data %>%
      dplyr::filter(.[[parameter]] == group) %>%
      dplyr::filter(p_value <= pval_thres) %>%
      dplyr::filter(variance >= 0) %>%
      iatlas.app::create_plotly_label(
        ., paste(.$feature_display, "- ", ancestry_df[cluster], "Ancestry"),
        paste("\n Immune Trait Category:",.$category, "\n Immune Trait Module:", .$module),
        c("variance", "se", "p_value","fdr"),
        title = "Immune Trait"
      )

  #creating the y label
  if(parameter == "cluster") df <- df %>% mutate(ylabel = feature_display)
  else if (parameter == "category" | parameter == "module")
    df <- df %>% mutate(ylabel = paste(ancestry_df[cluster], feature_display, sep = " - "))
  else  df <- df %>% mutate(ylabel = paste(ancestry_df[cluster], .[[parameter]], sep = " - "))
}

format_heritability_plot <- function(p, hdf, fdr = FALSE){
  p <- p %>%
    plotly::layout(
      xaxis = list(
        tickformat = "%"
      )
    )
    if(fdr == TRUE){
      p <- p %>%
            plotly::add_annotations(x = hdf$variance+hdf$se+0.01,
                                    y = hdf$ylabel,
                                    text = (hdf$plot_annot),
                                    xref = "x",
                                    yref = "y",
                                    showarrow = F,
                                    font=list(color='black')) #%>%
            # plotly::add_annotations( text="LRT FDR \n † <= 0.1 \n * <= 0.05 \n ** <= 0.01 \n *** <= 0.001", xref="paper", yref="paper",
            #                          x=1.03, xanchor="left",
            #                          y=0, yanchor="bottom",
            #                          legendtitle=TRUE, showarrow=FALSE )
    }
  p
}

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
  bp_max) {

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
    dplyr::mutate(x_col=bp_col+tot) %>%
    # Add highlight and annotation information
    dplyr::mutate( log10p = -log10(PLINK.P),
                   text = paste("<b>",display, "</b>",
                                "\n(Immune Trait Category: ", `Annot.Figure.ImmuneCategory`, ")",
                                "\nSNP name: ", snp_id, "\nSNP: ", snp_col, "\nPosition: ", bp_col, "\nChromosome: ", chr_col,
                                "\nPLINK MAF: ", maf, sep="")) #%>%

  # Filter SNP to make the plot lighter
  #filter(-log10(P)>0.5)

  df

}

