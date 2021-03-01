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
