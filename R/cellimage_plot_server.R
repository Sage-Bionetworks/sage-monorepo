cellimage_plot_server <- function(id, dataset, tag) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      cellimage_genes <- shiny::reactive(get_cellimage_genes())

      cellimage_features <- shiny::reactive(get_cellimage_features())

      gene_nodes <- shiny::reactive({
        shiny::req(dataset(), tag(), cellimage_genes())
        get_cellimage_gene_nodes(dataset(), tag(), cellimage_genes())
      })

      feature_nodes <- shiny::reactive({
        shiny::req(dataset(), tag(), cellimage_features())
        get_cellimage_feature_nodes(dataset(), tag(), cellimage_features())
      })

      nodes <- shiny::reactive({
        shiny::req(gene_nodes(), feature_nodes())
        dplyr::bind_rows(gene_nodes(), feature_nodes())
      })

      plot_obj <- shiny::reactive({
        range_tbl <- tibble::tibble(
          "node_feature_name" = nodes() %>%
            dplyr::pull("node_feature_name") %>%
            unique(),
          "MinBound" = 0,
          "MaxBound" = 1
        )

        annotations <- "cellimage_annotations" %>%
          get_tsv_path() %>%
          readr::read_tsv(.)

        labels <- "cellimage_labels" %>%
          get_tsv_path() %>%
          readr::read_tsv(.) %>%
          dplyr::pull("label")

        pic <- "tcell-cairo" %>%
          get_svg_path() %>%
          grImport2::readPicture()


        image_grob <- grImport2::pictureGrob(pic)
        gTree_name <- grid::childNames(image_grob)
        pathlabels <- image_grob$children[[gTree_name]]$childrenOrder
        fill_color <- character(43)
        names(fill_color) <- pathlabels[1:43]

        for (ind in seq(1, length(labels))){
          ioa      <- labels[ind]
          datavar  <- annotations %>% dplyr::filter(.data$display == ioa) %>% purrr::pluck("name")
          colormap <- annotations %>% dplyr::filter(.data$display == ioa) %>% purrr::pluck("color")
          alpha    <- annotations %>% dplyr::filter(.data$display == ioa) %>% purrr::pluck("alpha")
          color    <- getVarColor(datavar, colormap, nodes(), range_tbl, alpha)
          fill_color[ind] <- color
        }

        for (s in pathlabels[1:43] ){
          image_grob$children[[gTree_name]]$children[[s]]$gp$fill <- fill_color[s]
        }

        return(image_grob)
      })

      output$plot <- shiny::renderPlot(grid::grid.draw(plot_obj()))
    }
  )
}
