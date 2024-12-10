test_that("cellimage_network_server_tcga", {
  shiny::testServer(
    cellimage_network_server,
    args = list(
      "dataset" = shiny::reactiveVal("TCGA"),
      "tag" = shiny::reactiveVal("C1")
    ),
    {
      expect_named(
        feature_nodes(),
        c(
          "node_name",
          "node_feature_name",
          "node_feature_display",
          "tag",
          "x",
          "y",
          "score",
          "Type"
        )
      )
      expect_named(
        gene_nodes(),
        c(
          "node_name",
          "node_feature_name",
          "node_feature_display",
          "tag",
          "x",
          "y",
          "score",
          "Type"
        )
      )
      expect_named(
        edges(),
        c("node_display1", "node_display2", "node1", "node2", "tag")
      )
      expect_type(graph_json(), "character")
      expect_type(output$network, "character")
    }
  )
})

test_that("cellimage_network_server_pcawg", {
  shiny::testServer(
    cellimage_network_server,
    args = list(
      "dataset" = shiny::reactiveVal("PCAWG"),
      "tag" = shiny::reactiveVal("C1")
    ),
    {
      expect_named(
        feature_nodes(),
        c(
          "node_name",
          "node_feature_name",
          "node_feature_display",
          "tag",
          "x",
          "y",
          "score",
          "Type"
        )
      )
      expect_named(
        gene_nodes(),
        c(
          "node_name",
          "node_feature_name",
          "node_feature_display",
          "tag",
          "x",
          "y",
          "score",
          "Type"
        )
      )
      expect_named(
        edges(),
        c("node_display1", "node_display2", "node1", "node2", "tag")
      )
      expect_type(graph_json(), "character")
      expect_type(output$network, "character")
    }
  )
})
