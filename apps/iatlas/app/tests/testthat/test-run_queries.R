with_test_db_env({

  test_that("Build Sample Name Table", {
    result1 <- build_sample_name_tbl()
    expect_named(result1, c("sample_id", "sample_name"))
  })

  test_that("Build Feature Value Tibble from Feature IDs", {
    result1 <- build_feature_value_tbl_from_ids(1:5)
    expect_named(result1, c("sample_id", "feature_id", "value"))
    expect_true(all(result1$feature_id %in% 1:5))
  })

  test_that("Build Feature Value Tibble from Class IDs", {
    result1 <- build_feature_value_tbl_from_class_ids(1)
    expect_named(
      result1,
      c("feature_id", "sample_id", "value", "unit", "feature", "order")
    )
  })

  test_that("Build Feature Tibble", {
    result1 <- build_feature_tbl()
    expect_named(result1, c("class", "display", "feature"))
    result2 <- build_feature_tbl(1)
    class_tbl <- "SELECT name FROM classes WHERE id = 1" %>%
      perform_query("Build Class Tibble")
    expect_named(result2, c("class", "display", "feature"))
    expect_true(all(result2$class %in% class_tbl$name))
  })

  test_that("Create Class List", {
    result1 <- create_class_list()
    expect_type(result1, "integer")
    expect_type(names(result1), "character")
  })

  test_that("Build Gene Expression Table by Gene IDs", {
    result1 <- build_gene_expression_tbl_by_gene_ids(1:5)
    expect_named(result1, c("gene_id", "sample_id", "rna_seq_expr"))
    expect_true(all(result1$gene_id %in% 1:5))
  })

  test_that("Get Sample IDs from Dataset", {
    result1 <- get_sample_ids_from_dataset("TCGA")
    expect_type(result1, "integer")
    result2 <- get_sample_ids_from_dataset("Not a dataset")
    expect_length(result2, 0)
    expect_type(result2, "integer")
    result3 <- get_sample_ids_from_dataset("PCAWG")
    expect_type(result3, "integer")
    result4 <- get_sample_ids_from_dataset("Immune Subtype")
    expect_type(result4, "integer")
  })

  test_that("Translate Value", {
    result1 <- translate_value("A2M", "character", "genes", "id", "hgnc")
    expect_length(result1, 1)
    expect_type(result1, "integer")
    result2 <- translate_value(1L, "id", "genes", "hgnc", "id")
    expect_length(result2, 1)
    expect_type(result2, "character")
    expect_error(
      translate_value(0L, "id", "genes", "hgnc", "id"),
      regexp = "value not greater than 0",
      fixed = T
    )
    expect_error(
      translate_value(1L, "charcater", "genes", "hgnc", "id"),
      regexp = "Value of unallowed type.",
      fixed = T
    )
    expect_error(
      translate_value(1L, "charcater", "genes", "hgnc", "id"),
      regexp = "Value of unallowed type.",
      fixed = T
    )
  })

  test_that("Get HGNC Symbol from Gene ID", {
    result1 <- get_gene_hgnc_from_id(1L)
    expect_length(result1, 1)
    expect_type(result1, "character")
    expect_error(
      get_gene_hgnc_from_id(-1L),
      regexp = "value not greater than 0",
      fixed = T
    )
  })

  test_that("Get Gene ID from HGNC Symbol", {
    result1 <- get_gene_id_from_hgnc("A2M")
    expect_length(result1, 1)
    expect_type(result1, "integer")
    expect_error(
      get_gene_id_from_hgnc("Not a gene"),
      regexp = "length(result) not equal to 1",
      fixed = T
    )
  })

  test_that("Get Class ID from Class Name", {
    result1 <- get_class_id_from_name("DNA Alteration")
    expect_length(result1, 1)
    expect_type(result1, "integer")
    expect_error(
      get_class_id_from_name("Not a class"),
      regexp = "length(result) not equal to 1",
      fixed = T
    )
  })

  test_that("Get Feature Display Name From ID", {
    result1 <- get_feature_display_from_id(1L)
    expect_length(result1, 1)
    expect_type(result1, "character")
    expect_error(
      get_feature_display_from_id(-1L),
      regexp = "value not greater than 0",
      fixed = T
    )
    expect_error(
      get_feature_display_from_id(1),
      regexp = "value is not an integer vector",
      fixed = T
    )
  })

  test_that("Get Feature ID From Display Name", {
    result1 <- get_feature_id_from_display("Leukocyte Fraction")
    expect_length(result1, 1)
    expect_type(result1, "integer")
    expect_error(
      get_feature_id_from_display("Not a feature"),
      regexp = "length(result) not equal to 1",
      fixed = T
    )
    expect_error(
      get_feature_id_from_display(c("B Cells Memory", "Leukocyte Fraction")),
      regexp = "length(value) not equal to 1",
      fixed = T
    )
    expect_error(
      get_feature_id_from_display("B Cells"),
      regexp = "length(result) not equal to 1",
      fixed = T
    )
  })
})

