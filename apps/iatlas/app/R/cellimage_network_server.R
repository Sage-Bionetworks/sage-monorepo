cellimage_network_server <- function(id, dataset, tag) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      cellimage_genes <- shiny::reactive(get_cellimage_genes())

      cellimage_features <- shiny::reactive(get_cellimage_features())

      gene_nodes <- shiny::reactive({
        shiny::req(dataset(), tag(), cellimage_genes())
        nodes <- get_cellimage_gene_nodes(dataset(), tag(), cellimage_genes())

        if(nrow(nodes) == length(cellimage_genes())) nodes
        else add_nonexistent_gene_nodes(nodes, cellimage_genes())
      })

      feature_nodes <- shiny::reactive({
        shiny::req(dataset(), tag(), cellimage_features())
        nodes <- get_cellimage_feature_nodes(dataset(), tag(), cellimage_features())

        if(nrow(nodes) == length(cellimage_features())) nodes
        else add_nonexistent_feature_nodes(nodes, cellimage_features())
      })

      nodes <- shiny::reactive({
        shiny::req(gene_nodes(), feature_nodes())
        dplyr::bind_rows(gene_nodes(), feature_nodes())
      })

      edges <- shiny::reactive({
        shiny::req(nodes())
        get_cellimage_edges()
      })

      graph_json <- shiny::reactive({
        shiny::req(nodes(), edges())
        create_cellimage_json(nodes(), edges())
      })

      output$network <- cyjShiny::renderCyjShiny({
        cyjShiny::cyjShiny(
          graph_json(),
          layoutName = "preset",
          styleFile = get_javascript_path("style_network_cellimage")
        )
      })
    }
  )
}
