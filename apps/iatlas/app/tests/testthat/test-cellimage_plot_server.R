test_that("cellimage_plot_server_tcga", {
  shiny::testServer(
    cellimage_plot_server,
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
      expect_type(plot_obj(), "list")
      expect_type(output$plot, "list")
    }
  )
})

test_that("cellimage_plot_server_pcawg", {
  shiny::testServer(
    cellimage_plot_server,
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
      expect_type(plot_obj(), "list")
    }
  )
})
