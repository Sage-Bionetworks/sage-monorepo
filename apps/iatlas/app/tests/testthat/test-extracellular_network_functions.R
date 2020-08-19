with_test_api_env({

  test_that("build_ecn_gene_choice_list", {
    result <- build_ecn_gene_choice_list()
    expect_named(result, c("Genesets", "Genes"))
    expect_named(
      result$Genesets,
      c("Extracellular Network Genes", "Immunomodulator Genes")
    )
    expect_named(result$Genes)
  })

  test_that("build_ecn_celltype_choice_list", {
    result <- build_ecn_celltype_choice_list()
    expect_named(result)
    expect_true(result[[1]] == "All")
    expect_true(names(result)[[1]] == "All")
  })

  test_that("get_selected_gene_ids", {
    gene_list1 <- list()
    gene_list2 <- list("geneset:immunomodulator")
    gene_list3 <- list("gene:1")
    gene_list4 <- list("geneset:immunomodulator", "gene:1")
    result1 <- get_selected_gene_ids(gene_list1)
    result2 <- get_selected_gene_ids(gene_list2)
    result3 <- get_selected_gene_ids(gene_list3)
    result4 <- get_selected_gene_ids(gene_list4)
    expect_null(result1)
    expect_length (result2, 78)
    expect_equal(result3, 1)
    expect_length(result4, 79)
  })

  test_that("get_selected_celltypes", {
    list1 <- list()
    list2 <- list("All")
    list3 <- list("cell1")
    list4 <- list("All", "cell1")
    result1 <- get_selected_celltypes(list1)
    result2 <- get_selected_celltypes(list2)
    result3 <- get_selected_celltypes(list3)
    result4 <- get_selected_celltypes(list4)
    expect_null(result1)
    expect_length (result2, 9)
    expect_equal(result3, "cell1")
    expect_length(result4, 9)
  })



})
