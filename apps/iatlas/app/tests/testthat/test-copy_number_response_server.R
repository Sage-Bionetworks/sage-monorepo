
test_that("copy_number_response_server", {
  shiny::testServer(
    copy_number_response_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(
        iatlas.modules2::tcga_immune_subtype_cohort_obj_small
      )
    ),
    {
      expect_type(feature_class_list(), "list")
      expect_type(output$response_option_ui, "list")
      expect_type(group_tbl(), "list")
      expect_equal(nrow(group_tbl()), 6)
      expect_named(group_tbl(), c("display", "name"))
      expect_type(group_choice_list(), "character")
      expect_length(group_choice_list(), 7)
      expect_type(output$select_cn_group_ui, "list")
      expect_type(gene_tbl(), "list")
      expect_type(gene_set_tbl(), "list")
      expect_type(gene_choice_list(), "list")
      expect_type(output$select_cn_gene_ui, "list")

      session$setInputs("gene_filter_choices" = "All")
      session$setInputs("group_choices" = "All")
      session$setInputs("cn_dir_point_filter" = "All")
      session$setInputs("response_variable" = "leukocyte_fraction")

      expect_type(gene_entrez_query(), "integer")
      expect_true(length(gene_entrez_query()) > 1)
      expect_equal(groups(), c("C1", "C2", "C3", "C4", "C5", "C6"))
      expect_equal(direction_query(), NA)

      expect_type(result_tbl(), "list")
      expect_type(output$text, "character")
      expect_type(output$cnvPlot, "character")
      expect_type(data_table(), "list")
      expect_type(output$cnvtable, "character")

      session$setInputs("gene_filter_choices" = 1)
      session$setInputs("group_choices" = "C1")
      session$setInputs("cn_dir_point_filter" = 'Amp')

      expect_type(gene_entrez_query(), "integer")
      expect_equal(gene_entrez_query(), 1)
      expect_equal(groups(), "C1")
      expect_equal(direction_query(), 'Amp')

      expect_error(result_tbl())
    }
  )
})
