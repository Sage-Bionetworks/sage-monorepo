get_io_overview_table <- function(group, ioresponse_data){
  ioresponse_data$fmx_df %>%
    dplyr::mutate("Sample Group" = dplyr::case_when(
      is.na(.[[group]]) ~ "Not annotated",
      !is.na(.[[group]]) ~ as.character(.[[group]])
    )) %>%
    dplyr::group_by(Dataset, `Sample Group`) %>%
    dplyr::summarise(n = dplyr::n_distinct(Sample_ID)) %>%
    tidyr::pivot_wider(
      names_from = Dataset,
      values_from = n) %>%
    dplyr::mutate("Group Name" = purrr::map_chr(.[["Sample Group"]], function(x){
      if(x == "Not annotated") return("")
      convert_value_between_columns(input_value = x,
                                                df = ioresponse_data$sample_group_df %>% filter(Category %in% group),
                                                from_column = "FeatureValue",
                                                to_column = "FeatureName")}),
      "Plot Color" = purrr::map_chr(.[["Sample Group"]], function(x){
        if(x == "Not annotated")return("#C9C9C9")
        convert_value_between_columns(input_value = x,
                                                  df = ioresponse_data$sample_group_df %>% filter(Category %in% group),
                                                  from_column = "FeatureValue",
                                                  to_column = "FeatureHex")}),
      "Order" = purrr::map_chr(.[["Sample Group"]], function(x){
        if(x == "Not annotated")return("0")
        convert_value_between_columns(input_value = x,
                                                  df = ioresponse_data$sample_group_df %>% filter(Category %in% group),
                                                  from_column = "FeatureValue",
                                                  to_column = "order_within_sample_group") %>% as.integer()
      })
    )
}

get_io_mosaic_df <- function(ioresponse_data, group1, group2){

  #getting the labels
  label1 <- get_group_labels(ioresponse_data$sample_group_df, group1)
  label2 <- get_group_labels(ioresponse_data$sample_group_df, group2)

  not_annot <- data.frame("FeatureValue" = NA_character_,
                          "FeatureLabel" = "Not annotated",
                          "FeatureHex"="#C9C9C9",
                          "order_within_sample_group" = 0)
  label1 <- rbind(label1, not_annot)
  label2 <- rbind(label2, not_annot)

  df_mosaic <- merge(ioresponse_data$fmx_df %>%
                       dplyr::select(Sample_ID, Dataset, group1, group2),
                     label1, by.x = group1, by.y = "FeatureValue")

  df_mosaic <- merge(df_mosaic,
                     label2, by.x = group2, by.y = "FeatureValue")

  df_mosaic <- df_mosaic %>%
    dplyr::mutate(x= paste(Dataset, FeatureLabel.y)) %>%
    dplyr::arrange(order_within_sample_group.y) %>%
    dplyr::select(x, y = FeatureLabel.x, plot_color = FeatureHex.x)

  df_mosaic$x <-  as.factor(df_mosaic$x)
  df_mosaic$y <-  factor(df_mosaic$y, levels = (label1 %>%
                                                  dplyr::arrange(order_within_sample_group))$FeatureLabel)

  df_mosaic
}

create_mosaicplot <- function(
  df,
  title = NULL,
  fill_colors = NA,
  xlab = NULL,
  ylab = NULL) {

  plot <- df %>%
    ggplot2::ggplot() +
    ggmosaic::geom_mosaic(ggplot2::aes_string(x = stringr::str_c("ggmosaic::product(y, x)") ,fill = "y")) +
    #ggmosaic::geom_mosaic(aes_string(x = "ggmosaic::product(y, x)" ,fill = "y")) +
    ggmosaic::scale_y_productlist(expand = c(0, 0)) +
    ggmosaic::scale_x_productlist(expand = c(0, 0)) +
    labs(title) +
    xlab(xlab) +
    ylab(ylab) +
    theme_minimal() +
    theme(
      legend.title = element_blank(),
      axis.text.x = element_text(angle = 90, hjust = 1),
      axis.text.y = element_blank(),
      axis.ticks.y = element_blank()
    )
  if (!is.na(fill_colors[[1]])) {
    plot <- plot + scale_fill_manual(values = fill_colors)
  }
  p <- plotly::plotly_build(plot) %>%
    plotly::layout(title = title,
           legend = list(traceorder = 'reversed')) %>%
    format_plotly()
  p["width"] <- list(NULL)
  p["height"] <- list(NULL)
  p$x$layout["width"] <- list(NULL)
  p$x$layout["height"] <- list(NULL)
  p
}
