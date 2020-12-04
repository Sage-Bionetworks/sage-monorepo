create_mosaicplot <- function(
    df,
    title = NULL,
    fill_colors = NA,
    xlab = NULL,
    ylab = NULL) {

    plot <- df %>%
        ggplot2::ggplot() +
        ggmosaic::geom_mosaic(ggplot2::aes_string(x = stringr::str_c("ggmosaic::product(y, x)") ,fill = "y")) +
        ggmosaic::scale_y_productlist(expand = c(0, 0)) +
        ggmosaic::scale_x_productlist(expand = c(0, 0)) +
        ggplot2::labs(title) +
        xlab(xlab) +
        ylab(ylab) +
        ggplot2::theme_minimal() +
        ggplot2::theme(
            legend.title = ggplot2::element_blank(),
            axis.text.x = ggplot2::element_text(angle = 90, hjust = 1),
            axis.text.y = ggplot2::element_blank(),
            axis.ticks.y = ggplot2::element_blank()
        )
    if (!is.na(fill_colors[[1]])) {
        plot <- plot + ggplot2::scale_fill_manual(values = fill_colors)
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

