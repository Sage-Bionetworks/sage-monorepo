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
    grDevices::colorRampPalette(c(c1, c2))(3)[2]
  })

}

combine_groups <- function(df, group2, cohort_obj){

  group1 <- cohort_obj$group_name
  cat <- cohort_obj$group_tbl

  if("Immune feature bin range" %in% cat$characteristics){
    cat1 <- data.frame(
      parent_tag_name = group1,
      short_name = unique(cohort_obj$sample_tbl$group_name),
      long_name = unique(cohort_obj$sample_tbl$group_name),
      characteristics = "Immune feature bin range",
      color = viridis::viridis(dplyr::n_distinct(cohort_obj$sample_tbl$group_name))
    )

  }else{
    cat1 <- cat %>%
      dplyr::mutate(parent_tag_name = group1) %>%
      select(-c(dataset_name, dataset_display, size, order))%>%
      dplyr::distinct()
  }

  cat2 <- iatlas.api.client::query_tags_with_parent_tags(parent_tags = group2) %>%
    dplyr::select(parent_tag_name, short_name = tag_short_display, long_name = tag_long_display, characteristics = tag_characteristics, color = tag_color)

  categories <- rbind(cat1, cat2)

  samples <- tidyr::crossing(var1 = cat1$short_name, var2 = cat2$short_name) %>%
    dplyr::inner_join(cat1, by = c("var1" = "short_name")) %>%
    dplyr::inner_join(cat2, by = c("var2" = "short_name")) %>%
    dplyr::mutate(group = paste(var1, var2, sep = " & \n"),
                  color = combine_colors(.$color.x,.$color.y))

   df %>%
    dplyr::inner_join(categories, by = c("group_name" = "short_name")) %>%
    dplyr::select(sample_name, dataset_name, "group1" = group_name, "group2" = tag_short_display) %>%
    dplyr::mutate(group = paste(group1, group2, sep = " & \n")) %>%
    dplyr::inner_join(samples, by ="group")
}



create_plot_onegroup <- function(dataset_data, cohort_obj, dataset_display, plot_type, dataset, feature, group1, reorder_function = "None",  ylabel){


  if (reorder_function == "None"){
    order_plot <- iatlas.api.client::query_tags_with_parent_tags(parent_tags = cohort_obj$group_name) %>%
      dplyr::select(tag_short_display, tag_order, tag_color) %>%
      dplyr::arrange(tag_order) %>%
      dplyr::select(group = tag_short_display, color = tag_color)

  } else {
    reorder_method <- switch(
      reorder_function,
      "Median" = median,
      "Mean" = mean,
      "Max" = max,
      "Min" = min
    )
    order_plot <- dataset_data %>%
      dplyr::group_by(group, color) %>%
      dplyr::summarise(m = reorder_method(.data[[feature]]), .groups = "drop") %>%
      dplyr::arrange(.data$m) %>%
      dplyr::select("group", "color")
  }

  xform <- list(automargin = TRUE,
                categoryorder = "array",
                categoryarray = order_plot$group)

  group_colors <- order_plot$color
  names(group_colors) <- order_plot$group

  plot_title <- (sub("\\ -.*", "", unname(dataset_display[dataset])))

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

create_plot_twogroup <- function(dataset_data, cohort_obj, dataset_display, plot_type, dataset, feature, group, group1, group2, reorder_function = "None", ylabel){

  samples <- dataset_data %>% #getting the order to display groups
    dplyr::select(var1, var2, group) %>%
    dplyr::distinct() %>%
    dplyr::inner_join(iatlas.api.client::query_tags_with_parent_tags(parent_tags = group1) %>%
                        dplyr::select(var1 = tag_short_display, order1 = tag_order),
                      by = "var1") %>%
    dplyr::inner_join(iatlas.api.client::query_tags_with_parent_tags(parent_tags = group2) %>%
                        dplyr::select(var2 = tag_short_display, order2 = tag_order),
                      by = "var2") %>%
    dplyr::arrange(order1, order2)

  #ordering plot
  if (reorder_function == "None"){
    order_plot <- samples %>%
      dplyr::select(group)
  } else {
    reorder_method <- switch(
      reorder_function,
      "Median" = median,
      "Mean" = mean,
      "Max" = max,
      "Min" = min
    )

    order_plot <- dataset_data %>%
      dplyr::group_by(group, color) %>%
      dplyr::inner_join(samples, by = "group") %>%
      dplyr::summarise(m = order1,
                       n = reorder_method(.data[[feature]]), .groups = "drop") %>%
      dplyr::arrange(.data$m,.data$n) %>%
      dplyr::ungroup() %>%
      dplyr::select(group)
  }

  xform <- list(automargin = TRUE,
                tickangle = 80,
                categoryorder = "array",
                categoryarray = order_plot$group
  )

  group_colors <- dataset_data %>%
    dplyr::select(group, color) %>%
    dplyr::distinct() %>%
    tibble::deframe()

  plot_title <- (sub("\\ -.*", "", unname(dataset_display[dataset])))

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

get_stat_test <- function(df, group_to_split, sel_feature, dataset, dataset_title, paired = FALSE, test = t.test, label = group_to_split){

  data_set <- df %>%
    filter(dataset_name == dataset)

  dataset_display <- (sub("\\ -.*", "", unname(dataset_title[dataset])))

  if(paired == TRUE){
    patients <- data_set %>%
      dplyr::inner_join(iatlas.api.client::query_sample_patients(samples = data_set$sample_name), by = "sample_name")

    paired_samples <- patients %>%
      dplyr::group_by(patient_name) %>%
      dplyr::summarise(samples = dplyr::n_distinct(sample_name)) %>%
      dplyr::filter(samples > 1)

    data_set <- patients %>%
      dplyr::filter(patient_name %in% paired_samples$patient_name)
  }

  if(dplyr::n_distinct(data_set[[group_to_split]])>1){
    split_data <- split(data_set, data_set[[group_to_split]])
    comb_groups <- utils::combn(1:length(split_data), 2)

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
