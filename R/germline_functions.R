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
      dplyr::mutate(plot_annot = dplyr::case_when(
        fdr <= 0.001 ~"***",
        fdr <= 0.01 ~"**",
        fdr <= 0.05 ~ "*",
        fdr <= 0.1 ~"†",
        fdr > 0.1 ~ ""
      )) %>%
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
                                    font=list(color='black')) %>%
            plotly::add_annotations( text="LRT FDR \n † <= 0.1 \n * <= 0.05 \n ** <= 0.01 \n *** <= 0.001", xref="paper", yref="paper",
                                     x=1.03, xanchor="left",
                                     y=0, yanchor="bottom",
                                     legendtitle=TRUE, showarrow=FALSE )
    }
  p
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
  to_select,
  to_highlight,
  to_exclude) {

  if(to_highlight == FALSE & !is.null(to_exclude)) gwas <- gwas_df %>% dplyr::filter(!(feature_display %in% to_exclude))
  else if(to_highlight == TRUE) gwas <- gwas_df %>% dplyr::filter(feature_display %in% to_select)
  else gwas <- gwas_df

  gwas %>%
    dplyr::filter(snp_chr %in% chr_selected) %>%
    dplyr::group_by(snp_chr) %>%
    dplyr::summarise(chr_len=max(snp_bp), .groups = "drop_last") %>%
    dplyr::mutate(tot=cumsum(as.numeric(chr_len))-chr_len) %>%
    dplyr::select(-chr_len) %>%
    dplyr::left_join(gwas, ., by = "snp_chr") %>%
    dplyr::arrange(snp_chr, snp_bp) %>%
    dplyr::mutate(x_col=snp_bp+tot) %>%
    dplyr::mutate( log10p = -log10(p_value),
                   text = paste("<b>",feature_display, "</b>",
                                "\n(Immune Trait Category: ", `category`, ")",
                                "\nSNP name: ", snp_rsid, "\nSNP: ", snp_name, "\nChromosome: ", snp_chr, "\nPosition: ", snp_bp,
                                "\nPLINK MAF: ", maf, sep=""))
}

get_mhtplot_xlabel <- function(
  selected_region = input$selection,
  gwas_df = gwas_mht(),
  x_min = selected_min(),
  x_max = selected_max()
){
  if(selected_region == "See all chromosomes"){
    gwas_df %>%
      dplyr::group_by(snp_chr) %>%
      dplyr::summarize(center=( max(x_col) + min(x_col) ) / 2 , .groups = "drop") %>%
      dplyr::rename(label = snp_chr)
  }else{
    breaks <- c(x_min, (x_min+x_max)/2, x_max)
    data.frame(
      label = paste(format(round(breaks / 1e6, 2), trim = TRUE), "Mb"),
      center = breaks,
      stringsAsFactors = FALSE
    )
  }
}


