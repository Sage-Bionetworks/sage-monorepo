test_that("extracellular_network_main_server_immune_subtype", {
  shiny::testServer(
    extracellular_network_main_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(tcga_immune_subtype_cohort_obj_50)
    ),
    {
      expect_equal(show_stratify_option(), F)
      expect_type(output$stratify_ui, "list")
      expect_error(output$stratify)
      expect_type(output$select_groups_ui, "list")
      expect_type(output$select_statify_groups_ui, "list")
      expect_type(output$select_style, "list")
      expect_type(output$select_genes, "list")
      expect_type(output$select_celltypes, "list")
      session$setInputs("selected_genes" = "gene:1")
      session$setInputs("selected_celltypes" = c(
        "B_cells_Aggregate2", "Dendritic_cells_Aggregate2"
      ))
      expect_equal(selected_genes(), 1)
      expect_equal(
        selected_celltypes(),
        c("B_cells_Aggregate2", "Dendritic_cells_Aggregate2" )
      )
      print(main_scaffold())

      # expect_type(output$response_option_ui, "list")
      # expect_type(group_tbl(), "list")
      # expect_type(output$select_cn_group_ui, "list")
      # expect_type(gene_tbl(), "list")
      # expect_type(gene_set_tbl(), "list")
      # expect_type(gene_choice_list(), "list")
      # expect_type(output$select_cn_gene_ui, "list")
      # session$setInputs("gene_filter_choices" = 1:100)
      # expect_type(gene_entrez_query(), "integer")
      # expect_equal(gene_entrez_query(), 1:100)
      # session$setInputs("group_choices" = "All")
      # session$setInputs("cn_dir_point_filter" = "All")
      # session$setInputs("response_variable" = "leukocyte_fraction")
      # expect_type(result_tbl(), "list")
      # expect_type(output$text, "character")
      # expect_type(output$cnvPlot, "character")
      # expect_type(data_table(), "list")
      # expect_type(output$cnvtable, "character")
    }
  )
})
