build_distribution_io_df <- function(
  df,
  feature,
  scale_func_choice = "None"
){

  scale_function <- switch(
    scale_func_choice,
    "None" = identity,
    "Log2" = log2,
    "Log2 + 1" = function(x) log2(x + 1),
    "Log10" = log10,
    "Log10 + 1" = function(x) log10(x + 1)
  )

  df %>%
    tidyr::drop_na() %>%
    dplyr::mutate(y = scale_function(feature)) %>%
    tidyr::drop_na() %>%
    dplyr::filter(!is.infinite(y))
}

combine_colors <- function(color1, color2){

  purrr::map2_chr(.x = color1, .y = color2, function(c1, c2){
    colorRampPalette(c(c1, c2))(3)[2]
  })

}

combine_groups <- function(df, group1, group2, label1, label2){

  label1 <- get_group_labels(df, group1)

  df <- merge(df, label1, by.x = group1, by.y = "FeatureValue")

  if(group2 == "None" | group1 == group2){
    df <- df %>%
      dplyr::mutate(group = df$FeatureLabel,
                    color = df$FeatureHex)
  }else if(group2 != "None"  & group1 != group2){
    label2 <- get_group_labels(df, group2)
    df <- merge(df, label2, by.x = group2, by.y = "FeatureValue")
    df <- df %>%
      dplyr::mutate(group = (paste(as.character(df$"FeatureLabel.x"), "& \n", as.character(df$"FeatureLabel.y"))),
                    color = combine_colors(as.character(FeatureHex.x), as.character(FeatureHex.y)))
  }
  df
}

create_plot_onegroup <- function(dataset_data, plot_type, dataset, feature, group1, ylabel){

  xform <- list(automargin = TRUE,
                categoryorder = "array",
                categoryarray = (dataset_data %>%
                                   dplyr::select(group, order_within_sample_group) %>%
                                   dplyr::group_by(group) %>%
                                   dplyr::summarise(m = min(order_within_sample_group)) %>%
                                   dplyr::arrange(m) %>%
                                   dplyr::pull(group))
  )

  group_colors <- unique((dataset_data %>%
                            dplyr::arrange(order_within_sample_group))$color)
  names(group_colors) <- xform$categoryarray

  plot_type(dataset_data,
            x_col = as.character(group1),
            y_col = feature,
            xlab = dataset_data[[group1]],
            ylab = ylabel,
            custom_data = as.character(dataset),
            fill_colors = group_colors,
            source = "p1",
            showlegend = F)  %>%
    add_title_subplot_plotly(dataset) %>%
    plotly::layout(
      xaxis = xform,
      margin = list(b = 10),
      plot_bgcolor  = "rgb(250, 250, 250)"
    )
}

create_plot_twogroup <- function(dataset_data, plot_type, dataset, feature, group, group1, group2, ylabel){

  samples <- (dataset_data %>% dplyr::group_by(dataset_data[[group1]], dataset_data[[group2]]) %>%
                dplyr::summarise(samples = dplyr::n()))
  colnames(samples) <- c("var1", "var2", "samples")

  #get number of groups to draw lines
  samples <- (dataset_data %>%
                dplyr::group_by(dataset_data[[group1]], dataset_data[[group2]]) %>%
                dplyr::summarise(m = min(order_within_sample_group.x),
                                n = min(order_within_sample_group.y),
                                samples = dplyr::n()) %>%
                dplyr::arrange(m,n))

  colnames(samples) <- c("var1", "var2", "samples")

  xform <- list(automargin = TRUE,
                tickangle = 80,
                categoryorder = "array",
                categoryarray = (dataset_data %>%
                                   dplyr::select(group, order_within_sample_group.x, order_within_sample_group.y) %>%
                                   dplyr::group_by(group) %>%
                                   dplyr::summarise(m = min(order_within_sample_group.x),
                                                    n = min(order_within_sample_group.y)) %>%
                                   dplyr::arrange(m, n) %>%
                                   dplyr::pull(group))
  )
  group_colors <- unique((dataset_data %>%
                            dplyr::arrange(order_within_sample_group.x, order_within_sample_group.y))$color)
  names(group_colors) <- xform$categoryarray

  dataset_data %>%
    plot_type(.,
              x_col = group,
              y_col = feature,
              xlab = (dataset_data[[group]]),
              ylab = ylabel,
              custom_data = as.character(dataset),
              fill_colors = group_colors,
              source = "p1",
              showlegend = F) %>%
    add_title_subplot_plotly(dataset) %>%
    plotly::layout(
      autosize = TRUE,
      shapes = lazyeval::lazy_eval(get_lines_pos(samples, -0.38)),
      xaxis = xform,
      plot_bgcolor  = "rgb(250, 250, 250)"
    )
}

log2foldchanges <- function(x,y){
  mean(log2(y+1))-mean(log2(x+1))
}

get_stat_test <- function(df, group_to_split, sel_feature, dataset, paired = FALSE, test = t.test, label = group_to_split){

  data_set <- df %>%
    filter(Dataset == dataset)

  if(paired == TRUE){
    #validate(need(group_to_split == "treatment_when_collected"), "The selected sample group has only one sample per patient. Please, select 'Independent'.")
    patients <- data_set %>%
      dplyr::group_by(Patient_ID) %>%
      dplyr::summarise(samples = dplyr::n_distinct(Sample_ID)) %>%
      dplyr::filter(samples > 1) %>%
      dplyr::select(Patient_ID)

    data_set <- data_set %>%
      dplyr::filter(Patient_ID %in% patients$Patient_ID)

  }

  if(dplyr::n_distinct(data_set[[group_to_split]])>1){
    split_data <- split(data_set, data_set[[group_to_split]])
    comb_groups <- combn(1:length(split_data), 2)

    purrr::map2_dfr(.x = comb_groups[1,], .y = comb_groups[2,], function(x,y){

      if(paired == TRUE & nrow(split_data[[x]]) != nrow(split_data[[y]])){
        test_data <- data.frame(Dataset = dataset,
                                Group1 = paste0("Not available for paired test. ", names(split_data)[x], " (", nrow(split_data[[x]]),")"),
                                Group2 = paste0(names(split_data)[y], " (", nrow(split_data[[y]]), ")"),
                                #Test = paste0("Not available for paired test. ", names(split_data)[x], " (", nrow(split_data[[x]]),") vs. ", names(split_data)[y], " (", nrow(split_data[[y]]), ")"),
                                statistic = NA,
                                p.value = NA,
                                stringsAsFactors = FALSE)
      }else if(nrow(split_data[[x]]) <=1 | nrow(split_data[[y]]) <=1){
        test_data <- data.frame(Dataset = dataset,
                                Group1 = paste0("Few samples to perform test. ", names(split_data)[x], " (", nrow(split_data[[x]]),")"),
                                Group2 = paste0(names(split_data)[y], " (", nrow(split_data[[y]]), ")"),
                                #Test = paste0("Few samples to perform test.", names(split_data)[x], " (", nrow(split_data[[x]]),") vs. ", names(split_data)[y], " (", nrow(split_data[[y]]), ")"),
                                statistic = NA,
                                p.value = NA,
                                stringsAsFactors = FALSE)
      }else{
        test_data <- broom::tidy(test(split_data[[x]][[sel_feature]],
                                      split_data[[y]][[sel_feature]],
                                      paired = paired)) %>%
          dplyr::select(statistic, p.value)

        test_data$Dataset <- as.character(dataset)
        test_data$Group1 <- paste0(names(split_data)[x], " (", nrow(split_data[[x]]),")")
        test_data$Group2 <- paste0(names(split_data)[y], " (", nrow(split_data[[y]]), ")")
        test_data$FoldChange <- log2foldchanges(split_data[[x]][[sel_feature]],
                                                split_data[[y]][[sel_feature]])

        test_data %>%
          mutate("-log10(pvalue)" = -log10(p.value)) %>%
          dplyr::mutate_if(is.numeric, round, digits = 3) %>%
          dplyr::select(Dataset, Group1, Group2,  "Log2(FoldChange)" = FoldChange, statistic, p.value, "-log10(pvalue)")
        #dplyr::select(Dataset, Group1, "Group 1 Size" =  n_samples1, Group2,  "Group 2 Size" = n_samples2, statistic, p.value, "-log10(pvalue)")
      }
    })
  }else{
    test_data <- data.frame(Dataset = dataset,
                            Group1 = "Sample group has only one level for this dataset.",
                            Group2 = NA,
                            statistic = NA,
                            p.value = NA,
                            stringsAsFactors = FALSE)
  }
}
