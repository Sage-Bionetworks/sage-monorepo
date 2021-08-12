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
    dplyr::mutate(y = scale_function(.[[feature]])) %>%
    tidyr::drop_na() %>%
    dplyr::filter(!is.infinite(y))
}

combine_colors <- function(color1, color2){

  purrr::map2_chr(.x = color1, .y = color2, function(c1, c2){
    colorRampPalette(c(c1, c2))(3)[2]
  })

}

combine_groups <- function(df, group1, group2){
  cat1 <- iatlas.api.client::query_tags_with_parent_tags(parent_tags = group1)
  cat2 <- iatlas.api.client::query_tags_with_parent_tags(parent_tags = group2)
  categories <- rbind(cat1, cat2)

  samples <- tidyr::crossing(var1 = cat1$tag_name, var2 = cat2$tag_name) %>%
    dplyr::inner_join(cat1, by = c("var1" = "tag_name")) %>%
    dplyr::inner_join(cat2, by = c("var2" = "tag_name")) %>%
    dplyr::mutate(tag_name = paste(var1, var2, sep = " & \n"),
                  color = combine_colors(.$tag_color.x,.$tag_color.y))

  df %>%
    dplyr::inner_join(categories, by = "tag_name") %>%
    tidyr::pivot_wider(names_from = parent_tag_name, values_from = tag_name) %>%
    dplyr::select(sample, "group1" = dplyr::ends_with(group1), "group2" = dplyr::ends_with(group2)) %>%
    dplyr::group_by(sample) %>%
    dplyr::summarise(groups = dplyr::across(dplyr::all_of(c("group1", "group2")), na.omit)) %>%
    dplyr::mutate(tag_name = paste(groups$group1, groups$group2, sep = " & \n")) %>%
    dplyr::inner_join(samples, by ="tag_name")
}



create_plot_onegroup <- function(dataset_data, plot_type, dataset, feature, group1, reorder_function = "None",  ylabel){

  if (reorder_function == "None"){
    order_plot <- dataset_data %>%
      dplyr::select(tag_short_display, tag_order, tag_color) %>%
      dplyr::group_by(tag_short_display, tag_color) %>%
      dplyr::summarise(m = min(tag_order)) %>%
      dplyr::arrange(m) %>%
      dplyr::select(tag_short_display, tag_color)
  } else {
    reorder_method <- switch(
      reorder_function,
      "Median" = median,
      "Mean" = mean,
      "Max" = max,
      "Min" = min
    )

    order_plot <- dataset_data %>%
      dplyr::group_by(tag_short_display, tag_color) %>%
      dplyr::summarise(m = reorder_method(.data[[feature]]), .groups = "drop") %>%
      dplyr::arrange(.data$m) %>%
      dplyr::select("tag_short_display", "tag_color")
  }

  xform <- list(automargin = TRUE,
                categoryorder = "array",
                categoryarray = order_plot$tag_short_display)

  group_colors <- order_plot$tag_color
  names(group_colors) <- order_plot$tag_short_display

  plot_title <- unique(sub("\\ -.*", "", dataset_data$dataset_display))

  plot_type(dataset_data,
            x_col = as.character(group1),
            y_col = feature,
            xlab = dataset_data[[group1]],
            ylab = ylabel,
            custom_data = as.character(dataset),
            fill_colors = group_colors,
            source = "p1",
            showlegend = F)  %>%
    add_title_subplot_plotly(plot_title) %>%
    plotly::layout(
      xaxis = xform,
      margin = list(b = 10),
      plot_bgcolor  = "rgb(250, 250, 250)"
    )
}

create_plot_twogroup <- function(dataset_data, plot_type, dataset, feature, group, group1, group2, reorder_function = "None", ylabel){

  samples <- dataset_data %>%
    dplyr::group_by(var1, var2) %>%
    dplyr::summarise(m = min(tag_order.x),
                     n = min(tag_order.y),
                     samples = dplyr::n()) %>%
    dplyr::arrange(m,n)

  colnames(samples) <- c("var1", "var2", "order1", "order2", "n_samples")

  #ordering plot
  if (reorder_function == "None"){
    order_plot <- dataset_data %>%
      dplyr::group_by(group, color) %>%
      dplyr::summarise(m = min(tag_order.x),
                       n = min(tag_order.y)) %>%
      dplyr::arrange(m,n) %>%
      dplyr::select(group, color)

  } else {
    reorder_method <- switch(
      reorder_function,
      "Median" = median,
      "Mean" = mean,
      "Max" = max,
      "Min" = min
    )

    order_plot <- dataset_data %>%
      dplyr::group_by(group, tag_color) %>%
      dplyr::inner_join(samples, by = "tag_name") %>%
      dplyr::summarise(m = min(tag_order.x),
                       n = reorder_method(.data[[feature]]), .groups = "drop") %>%
      dplyr::arrange(.data$m,.data$n) %>%
      dplyr::ungroup() %>%
      dplyr::select(tag_name, tag_color)
  }

  xform <- list(automargin = TRUE,
                tickangle = 80,
                categoryorder = "array",
                categoryarray = order_plot$group
  )

  group_colors <- (order_plot$color)
  names(group_colors) <- order_plot$group
  plot_title <- unique(sub("\\ -.*", "", dataset_data$dataset_display))

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
    add_title_subplot_plotly(plot_title) %>%
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
    filter(dataset_name == dataset)

  dataset_display <- unique(sub("\\ -.*", "", data_set$dataset_display))
  if(paired == TRUE){
    #validate(need(group_to_split == "treatment_when_collected"), "The selected sample group has only one sample per patient. Please, select 'Independent'.")
    patients <- data_set %>%
      dplyr::group_by(sample) %>%
      dplyr::summarise(samples = dplyr::n_distinct(sample)) %>%
      dplyr::filter(samples > 1) %>%
      dplyr::select(sample)

    data_set <- data_set %>%
      dplyr::filter(sample %in% patients$sample)
  }

  if(dplyr::n_distinct(data_set[[group_to_split]])>1){
    split_data <- split(data_set, data_set[[group_to_split]])
    comb_groups <- combn(1:length(split_data), 2)

    purrr::map2_dfr(.x = comb_groups[1,], .y = comb_groups[2,], function(x,y){

      if(paired == TRUE & nrow(split_data[[x]]) != nrow(split_data[[y]])){
        test_data <- data.frame(Dataset = dataset_display,
                                Group1 = paste0("Not available for paired test. ", names(split_data)[x], " (", nrow(split_data[[x]]),")"),
                                Group2 = paste0(names(split_data)[y], " (", nrow(split_data[[y]]), ")"),
                                #Test = paste0("Not available for paired test. ", names(split_data)[x], " (", nrow(split_data[[x]]),") vs. ", names(split_data)[y], " (", nrow(split_data[[y]]), ")"),
                                statistic = NA,
                                p.value = NA,
                                stringsAsFactors = FALSE)
      }else if(nrow(split_data[[x]]) <=1 | nrow(split_data[[y]]) <=1){
        test_data <- data.frame(Dataset = dataset_display,
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

        test_data$Dataset <- as.character(dataset_display)
        test_data$Group1 <- paste0(names(split_data)[x], " (", nrow(split_data[[x]]),")")
        test_data$Group2 <- paste0(names(split_data)[y], " (", nrow(split_data[[y]]), ")")
        test_data$FoldChange <- log2foldchanges(split_data[[x]][[sel_feature]],
                                                split_data[[y]][[sel_feature]])

        test_data %>%
          dplyr::mutate("-log10(pvalue)" = -log10(p.value)) %>%
          dplyr::mutate_if(is.numeric, round, digits = 3) %>%
          dplyr::select(Dataset, Group1, Group2,  "Log2(FoldChange)" = FoldChange, statistic, p.value, "-log10(pvalue)")
        #dplyr::select(Dataset, Group1, "Group 1 Size" =  n_samples1, Group2,  "Group 2 Size" = n_samples2, statistic, p.value, "-log10(pvalue)")
      }
    })
  }else{
    test_data <- data.frame(Dataset = dataset_display,
                            Group1 = "Sample group has only one level for this dataset.",
                            Group2 = NA,
                            statistic = NA,
                            p.value = NA,
                            stringsAsFactors = FALSE)
  }
}
