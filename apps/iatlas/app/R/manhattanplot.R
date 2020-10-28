create_manhattanplot <- function(
  df,
  x_label,
  y_min,
  y_max,
  x_name = NULL,
  y_name = NULL,
  plot_title = "",
  source_name = NULL
  ) {

  plotly::ggplotly(ggplot2::ggplot(df, aes(x=BPcum, y=log10p, text=text)) +
                      geom_point(data=subset(df, is_highlight=="no"), aes(color=as.factor(chr_col)), alpha=0.8, size=1.3) +
                      scale_color_manual(values = rep(c("#19a8c7", "#d2d2d2"), 22)) +
                      scale_x_continuous(label = x_label$label, breaks= x_label$center) +
                      scale_y_continuous(expand = c(0, 0.5), limits = c(y_min, y_max)) +
                      geom_point(data=subset(df, is_highlight=="yes"), color="orange", size=2) +
                      theme_bw() +
                      theme(
                        legend.position="none",
                        panel.border = element_blank(),
                        panel.grid.major.x = element_blank(),
                        panel.grid.minor.x = element_blank()
                      )+
                     labs(
                       title = plot_title,
                       y = y_name, x = x_name),
                    tooltip="text",
                    source = source_name)
}
