test_that("cellimage_main_server", {
  shiny::testServer(
    cellimage_main_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(tcga_immune_subtype_cohort_obj)
    ),
    {
      expect_equal(cohort_groups(), c("C1", "C2", "C3", "C4", "C5", "C6"))
      expect_type(output$select_group1_ui, "list")
      expect_type(output$select_group2_ui, "list")
      session$setInputs("group_selected1" = "C1")
      session$setInputs("group_selected2" = "C1")
      expect_named(
        feature_nodes1(),
        c("node_name", "node_display", "tag", "x", "y", "score", "Type")
      )
      expect_named(
        gene_nodes1(),
        c("node_name", "node_display", "tag", "x", "y", "score", "Type")
      )
      expect_named(
        edges1(),
        c("node_display1", "node_display2", "node1", "node2", "tag")
      )
      expect_type(graph_json1(), "character")
      session$setInputs("viz_selection1" = "Network")
      expect_type(output$plot1, "list")

      # expect_named(
      #   feature_nodes2(),
      #   c("node_name", "node_display", "tag", "x", "y", "Type")
      # )
      # expect_named(
      #   gene_nodes2(),
      #   c("node_name", "node_display", "tag", "x", "y", "Type")
      # )
      # expect_named(
      #   edges2(),
      #   c("node_display1", "node_display2", "node1", "node2", "tag")
      # )

    }
  )
})
