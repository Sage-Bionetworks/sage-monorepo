get_feature_by_dataset <- function(features, feature_df, group_df, fmx_df, datasets_names){
  datasets <- unique(fmx_df$dataset_name)
  num_features <- features[which(features %in% feature_df$name)]
  cat_features <- features[which(features %in% group_df$parent_tag_name)]
  #Organize numerical features
  if(length(num_features)>0){
    num_cols <- tidyr::crossing(dataset = datasets, name = num_features) %>%
      dplyr::left_join(., feature_df %>% dplyr::select(name, display), by = "name") %>%
      dplyr::select(dataset,
                    group = name,
                    group_label = display) %>%
      dplyr::rowwise() %>%
      dplyr::mutate(feature= group,
                    dataset_display = names(datasets_names)[datasets_names == dataset],
                    ft_label = "Immune Feature")
  }
  #Check which datasets have more than one level for categorical features
  if(length(cat_features)>0){
    options <- tidyr::crossing(dataset = datasets, name = cat_features)
    cat_values <- purrr::map2_dfr(.x = options$dataset, .y = options$name, .f = function(x, y){
      uvalue <- unique((fmx_df %>%
                          dplyr::filter(dataset_name == x))[[y]])
      if(length(uvalue)>1) data.frame(dataset = as.character(x),
                                      dataset_display = names(datasets_names)[datasets_names == x],
                                      feature = as.character(y),
                                      gname = as.character(uvalue),
                                      stringsAsFactors = FALSE) %>%
        dplyr::mutate(group = paste0(feature, gname))
      else return()
    })
    if(nrow(cat_values)>0) cat_cols <- merge(cat_values, group_df,
              by.x = c("gname", "feature"), by.y = c("tag_name", "parent_tag_name")) %>%
        dplyr::mutate(group_label = paste(parent_tag_short_display, tag_short_display, sep = " - ")) %>%
        dplyr::select(dataset, dataset_display, feature, ft_label = parent_tag_short_display, group, group_label)

    else return()
    rbind(cat_cols, num_cols)
  }else{
    num_cols
  }
}

fit_coxph <- function(dataset1, data, feature, time, status, ft_labels, multivariate = FALSE){

  data_cox <- data %>%
    dplyr::filter(dataset_name == dataset1)

  if(!all(is.na(data_cox[[time]]))){
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
  }else{
    data.frame(
      logHR = NULL,
      logupper = NULL,
      loglower = NULL,
      difflog=NULL,
      logpvalue = NULL
    )
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
    dplyr::arrange(ft_label, dataset_display)#desc(abs(logHR)))

  if(dplyr::n_distinct(coxph_df$group) == 1){
    plot_title = ""
    ylabel = unique(subset_df$dataset_display)
  }else{
    plot_title = unique(subset_df$dataset_display)
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
    dplyr::select(dataset_display, group_label, logHR) %>%
    tidyr::pivot_wider(names_from = group_label, values_from = logHR) %>%
    as.data.frame()

  row.names(df) <- sub("\\ -.*", "", df$dataset_display)
  df$dataset_display <- NULL

  t(as.matrix(df))
}

add_BH_annotation <- function(coxph_df, p){

  fdr_corrected <- coxph_df %>%
    dplyr::select(dataset_display, group_label, pvalue, FDR, heatmap_annotation)
  fdr_corrected$dataset_display <- sub("\\ -.*", "", fdr_corrected$dataset_display)

  p %>%
    plotly::add_annotations(x = fdr_corrected$dataset_display,
                    y = fdr_corrected$group_label,
                    text = fdr_corrected$heatmap_annotation,
                    showarrow = F,
                    font=list(color='black')) %>%
    plotly::add_annotations( text="BH pValue \n * <= 0.2 \n ** <= 0.05", xref="paper", yref="paper",
                     x=1.03, xanchor="left",
                     y=0, yanchor="bottom",
                     legendtitle=TRUE, showarrow=FALSE )
}
