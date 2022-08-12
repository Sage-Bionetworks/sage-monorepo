# create_kmplot <- function(fit, df, confint, risktable, title, group_colors, show_pval = FALSE, show_pval_method = FALSE, facet = FALSE) {
#
#   if(facet ==FALSE){
#     survminer::ggsurvplot(
#       fit,
#       data = df,
#       conf.int = confint,
#       risk.table = risktable,
#       title = title,
#       palette = group_colors,
#       pval = show_pval,
#       pval.method = show_pval_method,
#     )
#   }else{
#     survminer::ggsurvplot_list(
#       fit,
#       data = df,
#       pval = show_pval,
#       pval.method = show_pval_method,
#       conf.int = confint,
#       risk.table = risktable,
#       title = title,
#       palette = group_colors
#     )
#   }
#
# }

