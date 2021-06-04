get_feature_by_dataset <- function(datasets, features, feature_df, group_df, fmx_df){

  all_comb <- tidyr::crossing(dataset = datasets, feature = features) %>%
    merge(., feature_df %>% dplyr::select(FeatureMatrixLabelTSV, FriendlyLabel,VariableType, `Variable Class Order`),
          by.x = "feature", by.y ="FeatureMatrixLabelTSV")

  num_cols <- all_comb[which(all_comb$VariableType == "Numeric"),]
  cat_cols <- all_comb[which(all_comb$VariableType == "Categorical"),]
  #Organize numerical features
  if(nrow(num_cols)>0){
    num_cols <- num_cols %>%
      dplyr::select(dataset,
                    group = feature,
                    group_label = FriendlyLabel,
                    order_within_sample_group = `Variable Class Order`) %>%
      dplyr::mutate(feature=group,
                    ft_label = "Immune Feature")

  }
  #Check which datasets have more than one level for categorical features
  if(nrow(cat_cols)>0){
    cat_values <- purrr::map2_dfr(.x = cat_cols$dataset, .y = cat_cols$feature, .f = function(x, y){
      uvalue <- unique((fmx_df %>%
                          dplyr::filter(Dataset == x))[[y]])

      if(length(uvalue)>1) data.frame(dataset = as.character(x),
                                      feature = as.character(y),
                                      gname = as.character(uvalue),
                                      stringsAsFactors = FALSE) %>%
        dplyr::mutate(group = paste0(feature, gname))
      else return()
    })

    if(nrow(cat_values)>0) cat_cols <- merge(cat_values, cat_cols, by = c("dataset", "feature")) %>%
        merge(., group_df,
              by.x = c("gname", "feature"), by.y = c("FeatureValue", "Category")) %>%
        dplyr::select(dataset, feature, ft_label = FriendlyLabel, group, group_label = FeatureLabel, order_within_sample_group)
    else return()

    rbind(cat_cols, num_cols)
  }else{
    num_cols
  }

}

fit_coxph <- function(dataset1, data, feature, time, status, ft_labels, multivariate = FALSE){

  data_cox <- data %>%
    dplyr::filter(Dataset == dataset1)

  #checking which features have more than one level for the dataset
  #valid_ft <- purrr::keep(feature, function(x) dplyr::n_distinct(data_cox[[x]])>1)
  valid_ft <- unique((ft_labels %>%
                        dplyr::filter(dataset == dataset1))$feature)

  if(multivariate == FALSE){
    purrr::map_dfr(.x = valid_ft, function(x){
      cox_features <- as.formula(paste(
        "survival::Surv(", time, ",", status, ") ~ ", x))

      survival::coxph(cox_features, data_cox)%>%
        create_ph_df(dataset = dataset1)
    })
  }else{
    mult_ft <- paste0(valid_ft, collapse  = " + ")

    cox_features <- as.formula(paste(
      "survival::Surv(", time, ",", status, ") ~ ",
      mult_ft)
    )
    survival::coxph(cox_features, data_cox) %>%
      create_ph_df(dataset = dataset1)
  }
}

create_ph_df <- function(coxphList, dataset){

  coef_stats <- as.data.frame(summary(coxphList)$conf.int)
  coef_stats$dataset <- dataset
  coef_stats$group <- row.names(coef_stats)
  coef_stats$pvalue <- (coef(summary(coxphList))[,5])

  coef_stats %>%
    dplyr::mutate(logHR = log10(`exp(coef)`),
                  logupper = log10(`upper .95`),
                  loglower = log10(`lower .95`),
                  difflog=logHR-loglower,
                  logpvalue = -log10(pvalue))
}

build_coxph_df <- function(datasets, data, feature, time, status, ft_labels, multivariate = FALSE){

  df <- purrr::map_dfr(.x = datasets, .f= fit_coxph,
                       data = data,
                       feature = feature,
                       time = time,
                       status = status,
                       ft_labels = ft_labels,
                       multivariate = multivariate) %>%
                       {suppressMessages(dplyr::right_join(x = ., ft_labels))} %>%
    dplyr::mutate(group_label=replace(group_label, is.na(logHR), paste("(Ref.)", .$group_label[is.na(logHR)]))) %>%
    dplyr::mutate_all(~replace(., is.na(.), 0))

  if(multivariate == FALSE){ #so we need to compute FDR and add annotation in the heatmap
    df_bh <-  df %>%
      dplyr::filter(!stringr::str_detect(group_label, '(Ref.)')) %>%
      dplyr::group_by(dataset) %>%
      dplyr::mutate(FDR = p.adjust(pvalue, method = "BH")) %>%
      dplyr::ungroup() %>%
      dplyr::select(dataset, group, FDR)

    df <- merge(df, df_bh, by = c("dataset", "group"), all.x = TRUE) %>%
      dplyr::mutate(heatmap_annotation = dplyr::case_when(
        is.na(FDR) ~ "",
        pvalue > 0.05 | FDR > 0.2 ~ "",
        pvalue <= 0.05 & FDR <= 0.2 & FDR > 0.05 ~ "*",
        pvalue <= 0.05 & FDR <= 0.05 ~ "**"
      ))
  }
  df
}

build_forestplot_dataset <- function(x, coxph_df, xname){

  subset_df <- coxph_df %>%
    dplyr::filter(dataset == x) %>%
    dplyr::arrange(ft_label, desc(abs(logHR)))

  if(dplyr::n_distinct(coxph_df$group) == 1){
    plot_title = ""
    ylabel = x
  }else{
    plot_title = x
    ylabel = factor(subset_df$group_label, levels = subset_df$group_label)
  }

  p <-  create_forestplot_plotly(x = subset_df$logHR,
                                 y = ylabel,
                                 error = subset_df$difflog,
                                 plot_title = plot_title,
                                 xlab = xname)

  if(dplyr::n_distinct(coxph_df$group) == 1){
    p <- p %>%
      plotly::layout(
        title = unique(subset_df$group_label)
      )
  }

  if(dplyr::n_distinct(subset_df$ft_label)>1){ #categorical data selected, add lines dividing categories
    p <- p %>%
      plotly::layout(
        shapes = lazyeval::lazy_eval(get_hlines_pos(subset_df %>% dplyr::select(var1 = ft_label)))
      )
  }
  p
}

build_heatmap_df <- function(coxph_df){
  df <- coxph_df %>%
    dplyr::filter(!stringr::str_detect(group_label, '(Ref.)')) %>%
    dplyr::arrange(feature) %>%
    dplyr::select(dataset, group_label, logHR) %>%
    tidyr::pivot_wider(names_from = group_label, values_from = logHR) %>%
    as.data.frame()

  row.names(df) <- df$dataset
  df$dataset <- NULL

  t(as.matrix(df))
}

add_BH_annotation <- function(coxph_df, p){

  fdr_corrected <- coxph_df %>%
    dplyr::select(dataset, group_label, pvalue, FDR, heatmap_annotation)

  p %>%
    plotly::add_annotations(x = fdr_corrected$dataset,
                    y = fdr_corrected$group_label,
                    text = fdr_corrected$heatmap_annotation,
                    showarrow = F,
                    font=list(color='black')) %>%
    plotly::add_annotations( text="BH pValue \n * <= 0.2 \n ** <= 0.05", xref="paper", yref="paper",
                     x=1.03, xanchor="left",
                     y=0, yanchor="bottom",
                     legendtitle=TRUE, showarrow=FALSE )
}
