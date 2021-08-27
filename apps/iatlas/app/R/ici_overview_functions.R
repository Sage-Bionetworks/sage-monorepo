get_io_overview_table <- function(values_for_group1){
  values_for_group1 %>%
  dplyr::group_by(dataset_display, Order = tag_order, Group = tag_short_display) %>%
  dplyr::mutate(dataset_display = sub("\\ -.*", "", dataset_display)) %>%
  dplyr::summarise(n = dplyr::n_distinct(sample_name)) %>%
  tidyr::pivot_wider(
    names_from = dataset_display,
    values_from = n)
}

get_io_mosaic_df <- function(values_for_group1, group2){
  iatlas.api.client::query_tag_samples(samples = values_for_group1$sample_name, parent_tags = group2) %>%
    merge(., values_for_group1, by = "sample_name") %>%
    dplyr::mutate(x= paste(sub("\\ -.*", "", dataset_display), tag_short_display.y)) %>%
    dplyr::select(x, y = tag_short_display.x, plot_color = tag_color.x)
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
