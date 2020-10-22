test_that("extracellular_network_main_server_immune_subtype", {
  shiny::testServer(
    extracellular_network_main_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(tcga_immune_subtype_cohort_obj_50)
    ),
    {
      expect_false(show_stratify_option())
      expect_false(stratify())
      expect_type(output$select_groups_ui, "list")

      expect_type(output$select_style, "list")
      expect_type(output$select_genes, "list")
      expect_type(output$select_celltypes, "list")
      session$setInputs("group_selected" = "C1")
      session$setInputs("selected_genes" = "gene:2")
      expect_equal(selected_genes(), 2)
      session$setInputs("selected_celltypes" = "All")
      expect_type(selected_celltypes(), "character")

      session$setInputs("abundance" = 0)
      session$setInputs("concordance" = 0)
      session$setInputs("calculate_button" = 1)
      expect_type(gene_nodes(), "list")
      expect_equal(nrow(gene_nodes()), 1)
      expect_type(feature_nodes(), "list")

      expect_type(edges(), "list")
      expect_equal(nrow(edges()), 1)
      expect_type(scaffold(), "list")
      expect_equal(nrow(scaffold()), 1)

      expect_type(output$select_node_ui, "list")
      expect_type(graph_json(), "character")
      session$setInputs("do_layout" = "cose")
      expect_type(output$cyjShiny, "character")

    }
  )
})

test_that("extracellular_network_main_server_tcga_study_no_stratification", {
  shiny::testServer(
    extracellular_network_main_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(tcga_study_cohort_obj_50)
    ),
    {
      expect_equal(show_stratify_option(), T)
      expect_type(output$stratify_ui, "list")
      session$setInputs("stratify" = F)
      expect_false(stratify())
      expect_type(output$select_groups_ui, "list")

      expect_type(output$select_style, "list")
      expect_type(output$select_genes, "list")
      expect_type(output$select_celltypes, "list")
      session$setInputs("group_selected" = "BRCA")
      session$setInputs("selected_genes" = "gene:2")
      expect_equal(selected_genes(), 2)
      session$setInputs("selected_celltypes" = "All")
      expect_type(selected_celltypes(), "character")
      session$setInputs("abundance" = 0)
      session$setInputs("concordance" = 0)
      session$setInputs("calculate_button" = 1)
      expect_type(gene_nodes(), "list")
      expect_equal(nrow(gene_nodes()), 1)
      expect_type(feature_nodes(), "list")
      expect_equal(nrow(feature_nodes()), 9)

      expect_type(edges(), "list")
      expect_equal(nrow(edges()), 1)

      expect_type(scaffold(), "list")
      expect_equal(nrow(scaffold()), 1)

      expect_type(output$select_node_ui, "list")
      expect_type(graph_json(), "character")
      session$setInputs("do_layout" = "cose")
      expect_type(output$cyjShiny, "character")

    }
  )
})

# TODO: fix stratified nodes/edges
test_that("extracellular_network_main_server_tcga_study_with_stratification", {
  shiny::testServer(
    extracellular_network_main_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(tcga_study_cohort_obj_50)
    ),
    {
      expect_equal(show_stratify_option(), T)
      expect_type(output$stratify_ui, "list")
      session$setInputs("stratify" = T)
      expect_true(stratify())
      expect_type(output$select_groups_ui, "list")
      expect_type(output$select_statify_groups_ui, "list")
      expect_type(output$select_style, "list")
      expect_type(output$select_genes, "list")
      expect_type(output$select_celltypes, "list")
      session$setInputs("group_selected" = "BRCA")
      session$setInputs("stratified_group_selected" = c("C1", "C2"))
      session$setInputs("selected_genes" = "gene:2")
      expect_equal(selected_genes(), 2)
      session$setInputs("selected_celltypes" = "All")
      expect_type(selected_celltypes(), "character")
      session$setInputs("abundance" = 0)
      session$setInputs("concordance" = 0)
      session$setInputs("calculate_button" = 1)

      # expect_type(gene_nodes(), "list")
      # expect_equal(nrow(gene_nodes()), 2)
      # expect_type(feature_nodes(), "list")
      # expect_equal(nrow(feature_nodes()), 18)
      #
      # expect_type(edges(), "list")
      # expect_equal(nrow(edges()), 2)
      #
      # expect_type(scaffold(), "list")
      # expect_equal(nrow(scaffold()), 2)
      #
      # expect_type(output$select_node_ui, "list")
      # expect_type(graph_json(), "character")
      # session$setInputs("do_layout" = "cose")
      # expect_type(output$cyjShiny, "character")
    }
  )
})
