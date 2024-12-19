test_that("extracellular_network_main_server_immune_subtype", {
  shiny::testServer(
    extracellular_network_main_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::tcga_immune_subtype_cohort_obj
      )
    ),
    {
      session$setInputs("group_selected" = "C1")
      session$setInputs("selected_genes" = "gene:2")
      session$setInputs("selected_celltypes" = "All")
      session$setInputs("abundance" = 0)
      session$setInputs("concordance" = 0)
      session$setInputs("calculate_button" = 1)
      session$setInputs("do_layout" = "cose")


      expect_false(show_stratify_option())
      expect_false(stratify())
      expect_type(gene_choice_list(), "list")
      expect_equal(group_choices(), c("C1", "C2", "C3", "C4", "C5", "C6"))

      expect_type(output$select_groups_ui, "list")
      expect_type(output$select_style, "list")
      expect_type(output$select_celltypes, "list")

      expect_equal(selected_genes(), 2)
      expect_type(selected_celltypes(), "character")

      expect_type(gene_nodes(), "list")
      expect_equal(nrow(gene_nodes()), 1)
      expect_type(feature_nodes(), "list")
      expect_equal(nrow(feature_nodes()), 9)
      expect_type(nodes(), "list")
      expect_equal(nrow(nodes()), 10)
      expect_named(
        nodes(),
        c(
          "label", "node_name", "node_display", "node_friendly", "tag", "score"
        )
      )

      expect_type(edges(), "list")
      expect_equal(nrow(edges()), 1)

      expect_type(output$select_node_ui, "list")
      expect_type(graph_json(), "character")
      expect_type(output$cyjShiny, "character")
    }
  )
})

test_that("extracellular_network_main_server_tcga_study_no_results", {
  shiny::testServer(
    extracellular_network_main_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::tcga_tcga_study_cohort_obj
      )
    ),
    {
      session$setInputs("stratify" = F)
      session$setInputs("group_selected" = "BRCA")
      session$setInputs("selected_genes" = "gene:2")
      session$setInputs("selected_celltypes" = "All")
      session$setInputs("abundance" = 100)
      session$setInputs("concordance" = 100)
      session$setInputs("calculate_button" = 1)
      session$setInputs("do_layout" = "cose")

      expect_equal(show_stratify_option(), T)
      expect_type(output$stratify_ui, "list")
      expect_false(stratify())
      expect_type(output$select_groups_ui, "list")

      expect_type(output$select_style, "list")
      expect_type(output$select_celltypes, "list")

      expect_type(gene_choice_list(), "list")
      expect_named(gene_choice_list(), c("Genesets", "Genes"))
      expect_length(gene_choice_list()$Genesets, 2)
      expect_true(length(gene_choice_list()$Genes) > 0)
      expect_equal(selected_genes(), 2)
      expect_type(selected_celltypes(), "character")

      expect_type(gene_nodes(), "list")
      expect_equal(nrow(gene_nodes()), 0)

      expect_type(feature_nodes(), "list")
      expect_equal(nrow(feature_nodes()), 0)
      expect_equal(nrow(nodes()), 0)
      expect_named(
        nodes(),
        c(
          "label", "node_name", "node_display", "node_friendly", "tag", "score"
        )
      )
      expect_error(
        edges(),
        "No network for this selection. Try changing the thresholds or selecting another subset.",
        "shiny.silent.error"
      )
    }
  )
})



test_that("extracellular_network_main_server_tcga_study_no_stratification", {
  shiny::testServer(
    extracellular_network_main_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::tcga_tcga_study_cohort_obj
      )
    ),
    {
      session$setInputs("stratify" = F)
      session$setInputs("group_selected" = "BRCA")
      session$setInputs("selected_genes" = "gene:2")
      session$setInputs("selected_celltypes" = "All")
      session$setInputs("abundance" = 0)
      session$setInputs("concordance" = 0)
      session$setInputs("calculate_button" = 2)
      session$setInputs("do_layout" = "cose")

      expect_equal(show_stratify_option(), T)
      expect_type(output$stratify_ui, "list")
      expect_false(stratify())
      expect_type(output$select_groups_ui, "list")

      expect_type(output$select_style, "list")
      expect_type(output$select_celltypes, "list")

      expect_equal(selected_genes(), 2)
      expect_type(selected_celltypes(), "character")

      expect_type(gene_nodes(), "list")
      expect_type(feature_nodes(), "list")
      expect_equal(nrow(gene_nodes()), 1)
      expect_equal(nrow(feature_nodes()), 9)

      expect_type(edges(), "list")
      expect_equal(nrow(edges()), 1)

      expect_type(output$select_node_ui, "list")
      expect_type(graph_json(), "character")

      expect_type(output$cyjShiny, "character")
    }
  )
})

test_that("extracellular_network_main_server_tcga_study_with_stratification", {
  shiny::testServer(
    extracellular_network_main_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::tcga_tcga_study_cohort_obj
      )
    ),
    {
      session$setInputs("stratify" = T)
      session$setInputs("group_selected" = "BRCA")
      session$setInputs("stratified_group_selected" = c("C1", "C2"))
      session$setInputs("selected_genes" = "gene:2")
      session$setInputs("abundance" = 0)
      session$setInputs("concordance" = 0)
      session$setInputs("selected_celltypes" = "All")
      session$setInputs("do_layout" = "cose")

      expect_equal(show_stratify_option(), T)
      expect_type(output$stratify_ui, "list")

      expect_true(stratify())
      expect_type(output$select_groups_ui, "list")
      expect_type(output$select_statify_groups_ui, "list")
      expect_type(output$select_style, "list")
      expect_type(output$select_celltypes, "list")

      expect_equal(selected_genes(), 2)
      expect_type(selected_celltypes(), "character")

      session$setInputs("calculate_button" = 1)

      expect_type(gene_nodes(), "list")
      expect_equal(nrow(gene_nodes()), 2)
      expect_type(feature_nodes(), "list")
      expect_equal(nrow(feature_nodes()), 18)
      expect_type(nodes(), "list")
      expect_equal(nrow(nodes()), 20)

      expect_type(edges(), "list")
      expect_equal(nrow(edges()), 2)

      expect_type(output$select_node_ui, "list")
      expect_type(graph_json(), "character")

      expect_type(output$cyjShiny, "character")
    }
  )
})

test_that("extracellular_network_main_server_pcawg_immune_subtype", {
  shiny::testServer(
    extracellular_network_main_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::pcawg_immune_subtype_cohort_obj
      )
    ),
    {
      expect_false(show_stratify_option())
      expect_false(stratify())
      expect_type(gene_choice_list(), "list")
      expect_type(output$select_groups_ui, "list")
      expect_type(output$select_style, "list")
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

      expect_type(output$select_node_ui, "list")
      expect_type(graph_json(), "character")
      session$setInputs("do_layout" = "cose")
      expect_type(output$cyjShiny, "character")
    }
  )
})
