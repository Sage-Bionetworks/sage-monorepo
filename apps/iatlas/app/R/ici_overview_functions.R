get_io_overview_table <- function(group){

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
      iatlas.app::convert_value_between_columns(input_value = x,
                                                df = ioresponse_data$sample_group_df %>% filter(Category == group),
                                                from_column = "FeatureValue",
                                                to_column = "FeatureName")}),
      "Plot Color" = purrr::map_chr(.[["Sample Group"]], function(x){
        if(x == "Not annotated")return("#C9C9C9")
        iatlas.app::convert_value_between_columns(input_value = x,
                                                  df = ioresponse_data$sample_group_df %>% filter(Category == group),
                                                  from_column = "FeatureValue",
                                                  to_column = "FeatureHex")}),
      "Order" = purrr::map_chr(.[["Sample Group"]], function(x){
        if(x == "Not annotated")return("0")
        iatlas.app::convert_value_between_columns(input_value = x,
                                                  df = ioresponse_data$sample_group_df %>% filter(Category == group),
                                                  from_column = "FeatureValue",
                                                  to_column = "order_within_sample_group") %>% as.integer()
      })
    )
}

get_io_mosaic_df <- function(fdf, group1, group2){

  #getting the labels
  label1 <- iatlas.app::get_group_labels(ioresponse_data$fmx_df, group1)
  label2 <- iatlas.app::get_group_labels(ioresponse_data$fmx_df, group2)

  not_annot <- data.frame("FeatureValue" = NA_character_,
                          "FeatureLabel" = "Not annotated",
                          "FeatureHex"="#C9C9C9",
                          "order_within_sample_group" = 0)
  label1 <- rbind(label1, not_annot)
  label2 <- rbind(label2, not_annot)

  df_mosaic <- merge(fdf %>%
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
