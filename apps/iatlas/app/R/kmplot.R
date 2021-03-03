create_kmplot <- function(fit, df, confint, risktable, title, group_colors, facet = FALSE) {

  if(facet ==FALSE){
    survminer::ggsurvplot(
      fit,
      data = df,
      conf.int = confint,
      risk.table = risktable,
      title = title,
      palette = group_colors
    )
  }else{
    survminer::ggsurvplot_list(
      fit,
      data = df,
      pval = TRUE,
      pval.method = TRUE,
      conf.int = confint,
      risk.table = risktable,
      title = title,
      palette = group_colors
    )
  }

}

